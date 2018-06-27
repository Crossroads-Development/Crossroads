package com.Da_Technomancer.crossroads.API.alchemy;

import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

public class SimpleReaction implements IReaction{

	protected final double heatChange;
	protected final double minTemp;
	protected final double maxTemp;
	protected final IReagent cat;
	protected final boolean charged;
	protected final Pair<IReagent, Integer>[] reagents;
	protected final Pair<IReagent, Integer>[] products;
	protected final int amountChange;

	protected static final double HEAT_CONVERSION = 5;//5 was picked somewhat arbitrarily as a conversion factor from J/mol * mol to *C

	public SimpleReaction(Pair<IReagent, Integer>[] reagents, Pair<IReagent, Integer>[] products, @Nullable IReagent cat, double minTemp, double maxTemp, double heatChange, boolean charged){
		this.reagents = reagents;
		this.products = products;
		this.cat = cat;
		this.minTemp = minTemp;
		this.maxTemp = maxTemp;
		this.heatChange = heatChange;
		this.charged = charged;
		int change = 0;

		for(Pair<IReagent, Integer> reag : reagents){
			change -= reag.getRight();
		}
		for(Pair<IReagent, Integer> prod : products){
			change += prod.getRight();
		}

		this.amountChange = change;
	}

	@Override
	public boolean performReaction(IReactionChamber chamb){

		//Check charged, catalyst, temperature, and solvent requirements
		if(charged && !chamb.isCharged()){
			return false;
		}

		ReagentStack[] reags = chamb.getReagants();
		if(cat != null && reags[cat.getIndex()] == null){
			return false;
		}
		double chambTemp = chamb.getTemp();
		if(chambTemp > maxTemp || chambTemp < minTemp){
			return false;
		}
		
		double content = chamb.getContent();

		double maxReactions = amountChange <= 0 ? 200 : (chamb.getReactionCapacity() - content) / (double) amountChange;

		for(Pair<IReagent, Integer> reag : reagents){
			if(reags[reag.getLeft().getIndex()] == null){
				return false;
			}
			maxReactions = Math.min(maxReactions, reags[reag.getLeft().getIndex()].getAmount() / (double) reag.getRight());
		}

		//temperature change based limit
		double allowedTempChange = heatChange < 0 ? maxTemp - chambTemp : minTemp - chambTemp;
		
		maxReactions = Math.min(maxReactions, -content * allowedTempChange / (heatChange * HEAT_CONVERSION));

		if(maxReactions <= 0D){
			return false;
		}

		//Sanity check, makes sure that the reaction batch is large enough that none of the products will be immediately wiped for being too small a quantity
		for(Pair<IReagent, Integer> reag : products){
			if(reags[reag.getLeft().getIndex()] == null && maxReactions * (double) reag.getRight() <= AlchemyCore.MIN_QUANTITY){
				return false;
			}
		}

		for(Pair<IReagent, Integer> reag : reagents){
			if(reags[reag.getLeft().getIndex()].increaseAmount(-maxReactions * (double) reag.getRight()) <= 0D){
				reags[reag.getLeft().getIndex()] = null;
			}
		}

		for(Pair<IReagent, Integer> reag : products){
			if(reags[reag.getLeft().getIndex()] == null){
				reags[reag.getLeft().getIndex()] = new ReagentStack(reag.getLeft(), maxReactions * (double) reag.getRight());
			}else{
				reags[reag.getLeft().getIndex()].increaseAmount(maxReactions * (double) reag.getRight());
			}
		}

		chamb.addHeat(-heatChange * maxReactions * HEAT_CONVERSION);
		chamb.addHeat(amountChange * (chamb.getHeat() / content) * maxReactions);
		return true;
	}
}
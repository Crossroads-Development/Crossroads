package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

public class SimpleReaction implements IReaction{

	protected final double heatChange;
	protected final double minTemp;
	protected final double maxTemp;
	protected final IReagent cat;
	protected final boolean charged;
	protected final EnumSolventType[] solvents;
	protected final Pair<IReagent, Integer>[] reagents;
	protected final Pair<IReagent, Integer>[] products;
	protected final int amountChange;

	protected static final double HEAT_CONVERSION = 5;//5 was picked somewhat arbitrarily as a conversion factor from J/mol * mol to *C

	public SimpleReaction(Pair<IReagent, Integer>[] reagents, Pair<IReagent, Integer>[] products, @Nullable IReagent cat, double minTemp, double maxTemp, double heatChange, @Nullable EnumSolventType[] solvents, boolean charged){
		this.reagents = reagents;
		this.products = products;
		this.cat = cat;
		this.minTemp = minTemp;
		this.maxTemp = maxTemp;
		this.heatChange = heatChange;
		this.solvents = solvents;
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
	public boolean performReaction(IReactionChamber chamb, boolean[] solventsIn){
		//Check charged, catalyst, temperature, and solvent requirements
		if(charged && !chamb.isCharged()){
			return false;
		}
		ReagentStack[] reags = chamb.getReagants();
		if(cat != null && reags[cat.getIndex()] == null){
			return false;
		}
		if(chamb.getTemp() > maxTemp || chamb.getTemp() < minTemp){
			return false;
		}
		if(solvents != null){
			for(EnumSolventType solv : solvents){
				if(!solventsIn[solv.ordinal()]){
					return false;
				}
			}
		}

		double maxReactions = amountChange == 0 ? 200 : (chamb.getReactionCapacity() - chamb.getContent()) / (double) amountChange;

		for(Pair<IReagent, Integer> reag : reagents){
			if(reags[reag.getLeft().getIndex()] == null){
				return false;
			}
			maxReactions = Math.min(maxReactions, reags[reag.getLeft().getIndex()].getAmount() / (double) reag.getRight());
		}

		//temperature change based limit
		double allowedTempChange = heatChange < 0 ? maxTemp - chamb.getTemp() : minTemp - chamb.getTemp();
		maxReactions = Math.min(maxReactions, -chamb.getContent() * allowedTempChange / (heatChange * HEAT_CONVERSION + (double) amountChange * allowedTempChange));

		if(maxReactions <= 0D){
			return false;
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

		chamb.addHeat((amountChange * chamb.getTemp() * maxReactions) - (heatChange * maxReactions * HEAT_CONVERSION));
		return true;
	}
}
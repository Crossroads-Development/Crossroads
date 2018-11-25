package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nullable;

public class SimpleReaction implements IReaction{

	protected final double heatChange;
	protected final double minTemp;
	protected final double maxTemp;
	protected final IReagent cat;
	protected final boolean charged;
	protected final ReagentStack[] reagents;
	protected final ReagentStack[] products;
	protected final int amountChange;

	protected static final double HEAT_CONVERSION = 5;//5 was picked somewhat arbitrarily as a conversion factor from J/mol * mol to *C

	public SimpleReaction(ReagentStack[] reagents, ReagentStack[] products, @Nullable IReagent cat, double minTemp, double maxTemp, double heatChange, boolean charged){
		this.reagents = reagents;
		this.products = products;
		this.cat = cat;
		this.minTemp = minTemp;
		this.maxTemp = maxTemp;
		this.heatChange = heatChange;
		this.charged = charged;
		int change = 0;

		for(ReagentStack reag : reagents){
			change -= reag.getAmount();
		}
		for(ReagentStack prod : products){
			change += prod.getAmount();
		}

		this.amountChange = change;
	}

	@Override
	public boolean performReaction(IReactionChamber chamb){
		//Check charged, catalyst, temperature, and solvent requirements
		if(charged && !chamb.isCharged()){
			return false;
		}

		ReagentMap reags = chamb.getReagants();
		if(cat != null && reags.getQty(cat) <= 0){
			return false;
		}
		double chambTemp = chamb.getTemp();
		if(chambTemp > maxTemp || chambTemp < minTemp){
			return false;
		}
		
		int content = chamb.getContent();

		int maxReactions = amountChange <= 0 ? 200 : (chamb.getReactionCapacity() - content) / amountChange;

		for(ReagentStack reag : reagents){
			if(reags.getQty(reag.getType()) <= 0){
				return false;
			}
			maxReactions = Math.min(maxReactions, reags.getQty(reag.getType()) / reag.getAmount());
		}

		//temperature change based limit
		double allowedTempChange = heatChange < 0 ? maxTemp - chambTemp : minTemp - chambTemp;
		
		maxReactions = (int) Math.min(maxReactions, -content * allowedTempChange / (heatChange * HEAT_CONVERSION));

		if(maxReactions <= 0){
			return false;
		}

		for(ReagentStack reag : reagents){
			reags.addReagent(reag.getType(), -maxReactions * reag.getAmount());
		}

		for(ReagentStack reag : products){
			reags.addReagent(reag.getType(), maxReactions * reag.getAmount());
		}

		chamb.addHeat(-heatChange * maxReactions * HEAT_CONVERSION);
		chamb.addHeat(amountChange * (chamb.getHeat() / content) * maxReactions);
		return true;
	}
}
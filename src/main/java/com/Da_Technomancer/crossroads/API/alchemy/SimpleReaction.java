package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.API.heat.HeatUtil;

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

		int maxReactions = amountChange <= 0 ? 200 : (chamb.getReactionCapacity() - content) / amountChange;//200 chosen arbitrarily as a moderately large positive number

		for(ReagentStack reag : reagents){
			if(reags.getQty(reag.getType()) <= 0){
				return false;
			}
			maxReactions = Math.min(maxReactions, reags.getQty(reag.getType()) / reag.getAmount());
		}

		if(heatChange != 0){
			//temperature change based limit
			double allowedTempChange = heatChange < 0 ? maxTemp - chambTemp : minTemp - chambTemp;
			maxReactions = Math.min(maxReactions, (int) Math.max(1, -content * allowedTempChange / (heatChange + amountChange * allowedTempChange)));
		}

		if(maxReactions <= 0){
			return false;
		}

		for(ReagentStack reag : products){
			reags.addReagent(reag.getType(), maxReactions * reag.getAmount(), reags.getTempC());
		}

		for(ReagentStack reag : reagents){
			reags.removeReagent(reag.getType(), maxReactions * reag.getAmount());
		}

		reags.setTemp(HeatUtil.toCelcius((reags.getTempK() * reags.getTotalQty() - heatChange * maxReactions) / reags.getTotalQty()));
		return true;
	}
}
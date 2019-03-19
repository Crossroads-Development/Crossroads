package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import net.minecraft.util.EnumParticleTypes;

import javax.annotation.Nullable;

public class DestroyReaction extends SimpleReaction implements ITransparentReaction{

	private static final float MAX_BLAST = 8;
	private final float blastPer;

	/**
	 * A reaction that will destroy the chamber it happens in. Used, for example, by burning gunpowder
	 * @param reagents Required reagents
	 * @param cat Required catalyst, null for none
	 * @param minTemp Minimum temp
	 * @param maxTemp Maximum temp
	 * @param charged Whether the chamber needs to be charged
	 */
	public DestroyReaction(ReagentStack[] reagents, @Nullable IReagent cat, double minTemp, double maxTemp, boolean charged, float blastPer){
		super(reagents, new ReagentStack[] {}, cat, minTemp, maxTemp, 0, charged);
		this.blastPer = blastPer;
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

		int maxReactions = 200;//200 chosen arbitrarily as a moderately large positive number

		for(ReagentStack reag : reagents){
			if(reags.getQty(reag.getType()) <= 0){
				return false;
			}
			maxReactions = Math.min(maxReactions, reags.getQty(reag.getType()) / reag.getAmount());
		}

		if(maxReactions <= 0){
			return false;
		}

		for(ReagentStack reag : reagents){
			reags.removeReagent(reag.getType(), maxReactions * reag.getAmount());
		}

		reags.setTemp(HeatUtil.toCelcius((reags.getTempK() * reags.getTotalQty() - heatChange * maxReactions) / reags.getTotalQty()));

		chamb.destroyChamber(Math.min(MAX_BLAST, blastPer * maxReactions));
		chamb.addVisualEffect(EnumParticleTypes.SMOKE_NORMAL, 0, 0, 0);

		return true;
	}

	@Override
	public IReagent getCatalyst(){
		return cat;
	}

	@Override
	public double minTemp(){
		return minTemp;
	}

	@Override
	public double maxTemp(){
		return maxTemp;
	}

	@Override
	public boolean charged(){
		return charged;
	}

	@Override
	public double deltaHeatPer(){
		return -1;//Show a negative delta heat in JEI so it will say "Exothermic"
	}

	@Override
	public ReagentStack[] getReagents(){
		return reagents;
	}

	@Override
	public ReagentStack[] getProducts(){
		return products;
	}

	@Override
	public boolean isDestructive(){
		return true;
	}
}
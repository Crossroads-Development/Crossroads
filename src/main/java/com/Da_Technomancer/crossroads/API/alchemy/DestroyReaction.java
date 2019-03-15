package com.Da_Technomancer.crossroads.API.alchemy;

import net.minecraft.util.EnumParticleTypes;

import javax.annotation.Nullable;

public class DestroyReaction extends SimpleReaction implements ITransparentReaction{

	/**
	 * A reaction that will destroy the chamber it happens in. Used, for example, by burning gunpowder
	 * @param reagents Required reagents
	 * @param cat Required catalyst, null for none
	 * @param minTemp Minimum temp
	 * @param maxTemp Maximum temp
	 * @param charged Whether the chamber needs to be charged
	 */
	public DestroyReaction(ReagentStack[] reagents, @Nullable IReagent cat, double minTemp, double maxTemp, boolean charged){
		super(reagents, new ReagentStack[] {}, cat, minTemp, maxTemp, 0, charged);
	}

	@Override
	public boolean performReaction(IReactionChamber chamb){
		boolean ran = super.performReaction(chamb);
		if(ran){
			chamb.destroyChamber();
			chamb.addVisualEffect(EnumParticleTypes.SMOKE_NORMAL, 0, 0, 0);
		}
		return ran;
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
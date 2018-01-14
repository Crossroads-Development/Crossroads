package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nonnull;

public class ReagentStack{

	private final IReagent type;
	private double amount;
	private EnumMatterPhase phase;

	public ReagentStack(IReagent type, double amount){
		this.type = type;
		this.amount = amount;
	}

	@Nonnull
	public IReagent getType(){
		return type;
	}

	/**
	 * @param temp The temperature (degrees C)
	 * @param solvents An array of the same size as {@link EnumSolventType#values()}, with each index representing the EnumSolventType of that ordinal
	 */
	public void updatePhase(double temp, boolean[] solvents){
		if(type.isLockedFlame()){
			phase = EnumMatterPhase.FLAME;
			return;
		}
		if(temp >= type.getBoilingPoint()){
			phase = EnumMatterPhase.GAS;
			return;
		}
		if(temp < type.getMeltingPoint()){
			EnumSolventType solvent = type.soluteType();
			if(solvent != null){
				if(solvents[solvent.ordinal()]){
					phase = EnumMatterPhase.SOLUTE;
				}else{
					phase = EnumMatterPhase.SOLID;
				}
			}else{
				phase = EnumMatterPhase.SOLID;
			}
			return;
		}

		phase = EnumMatterPhase.LIQUID;
	}

	/**
	 * @param temp Current temperature. Optional, only used if phase hasn't been set yet to update the phase. If phase should already have been set, this can be left as 0. 
	 * @return
	 */
	@Nonnull
	public EnumMatterPhase getPhase(double temp){
		if(phase == null){
			updatePhase(temp, new boolean[EnumSolventType.values().length]);
		}
		return phase;
	}

	/**
	 * @return The amount of this substance. In moles (where applicable). 
	 */
	public double getAmount(){
		return amount;
	}

	public void setAmount(double amountIn){
		amount = Math.max(0, amountIn);
	}

	/**
	 * @param amountChange
	 * @return The new amount. 
	 */
	public double increaseAmount(double amountChange){
		setAmount(amount + amountChange);
		return amount;
	}

	@Override
	public String toString(){
		return type == null ? "EMPTY_REAGENT" : (type.getName() + ", Amount: " + amount);
	}
}

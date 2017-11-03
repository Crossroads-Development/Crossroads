package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nonnull;

public class Reagent{

	private final IReagentType type;
	private double amount;
	private EnumMatterPhase phase;

	public Reagent(IReagentType type, double amount){
		this.type = type;
		this.amount = amount;
	}

	@Nonnull
	public IReagentType getType(){
		return type;
	}

	public void updatePhase(double temp, boolean polar, boolean nonPolar, boolean aquaRegia){
		if(temp >= type.getBoilingPoint()){
			phase = EnumMatterPhase.GAS;
			return;
		}
		if(temp < type.getMeltingPoint()){
			SolventType solvent = type.soluteType();
			if(solvent != null){
				switch(solvent){
					case AQUA_REGIA:
						if(aquaRegia){
							phase = EnumMatterPhase.SOLUTE;
						}else{
							phase = EnumMatterPhase.SOLID;
						}
						break;
					case MIXED_POLAR:
						if(polar || nonPolar){
							phase = EnumMatterPhase.SOLUTE;
						}else{
							phase = EnumMatterPhase.SOLID;
						}
						break;
					case POLAR:
						if(polar){
							phase = EnumMatterPhase.SOLUTE;
						}else{
							phase = EnumMatterPhase.SOLID;
						}
						break;
					case NON_POLAR:
						if(nonPolar){
							phase = EnumMatterPhase.SOLUTE;
						}else{
							phase = EnumMatterPhase.SOLID;
						}
						break;
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
			updatePhase(temp, false, false, false);
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

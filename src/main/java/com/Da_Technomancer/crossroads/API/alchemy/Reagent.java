package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nonnull;

public class Reagent{

	private final IReagentType type;
	private double temp;
	private double amount;
	private MatterPhase phase;

	public Reagent(IReagentType type, double temp, double amount){
		this.type = type;
		this.temp = temp;
		this.amount = amount;
	}

	@Nonnull
	public IReagentType getType(){
		return type;
	}

	public void updatePhase(boolean polar, boolean nonPolar, boolean aquaRegia){
		if(temp >= type.getBoilingPoint()){
			phase = MatterPhase.GAS;
			return;
		}
		if(temp < type.getMeltingPoint()){
			SolventType solvent = type.soluteType();
			switch(solvent){
				case AQUA_REGIA:
					if(aquaRegia){
						phase = MatterPhase.SOLUTE;
					}else{
						phase = MatterPhase.SOLID;
					}
					break;
				case MIXED_POLAR:
					if(polar || nonPolar){
						phase = MatterPhase.SOLUTE;
					}else{
						phase = MatterPhase.SOLID;
					}
					break;
				case POLAR:
					if(polar){
						phase = MatterPhase.SOLUTE;
					}else{
						phase = MatterPhase.SOLID;
					}
					break;
				case NON_POLAR:
					if(nonPolar){
						phase = MatterPhase.SOLUTE;
					}else{
						phase = MatterPhase.SOLID;
					}
					break;
				default:
					phase = MatterPhase.SOLID;
					break;
			}
			return;
		}

		phase = MatterPhase.LIQUID;
	}

	@Nonnull
	public MatterPhase getPhase(){
		if(phase == null){
			updatePhase(false, false, false);
		}
		return phase;
	}

	public double getTemp(){
		return temp;
	}

	public void setTemp(double tempIn){
		temp = Math.max(-273, tempIn);
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
	public double increaseAmount(double amountChange, double newMatTemp){
		if(amountChange > 0 ){
			setTemp(((temp * amount) + (amountChange * newMatTemp)) / (amount + amountChange));
		}
		setAmount(amount + amountChange);
		return amount;
	}
}

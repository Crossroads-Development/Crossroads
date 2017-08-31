package com.Da_Technomancer.crossroads.API.alchemy;

public class SimpleReagent implements IReagent{

	private final IReagentType type;
	private double temp;
	private double amount;
	private MatterPhase phase;

	public SimpleReagent(IReagentType type, double temp, double amount){
		this.type = type;
		this.temp = temp;
		this.amount = amount;
	}

	@Override
	public IReagentType getType(){
		return type;
	}

	@Override
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

	@Override
	public MatterPhase getPhase(){
		return phase;
	}

	@Override
	public double getTemp(){
		return temp;
	}

	@Override
	public void setTemp(double tempIn){
		temp = tempIn;
	}

	@Override
	public double getAmount(){
		return amount;
	}

	@Override
	public void setAmount(double amountIn){
		amount = amountIn;
	}
}

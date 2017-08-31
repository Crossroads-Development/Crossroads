package com.Da_Technomancer.crossroads.API.alchemy;

public class SimpleReagent implements IReagent{

	private final IReagentType type;
	private double temp;
	private double amount;
	
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
	public MatterPhase getPhase(){
		
		// TODO Auto-generated method stub
		return null;
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

package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nonnull;

public interface IReagent{
	
	@Nonnull
	public IReagentType getType();
	
	@Nonnull
	public MatterPhase getPhase();
	
	public double getTemp();
	
	public void setTemp(double tempIn);
	
	public double getAmount();
	
	public void setAmount(double amountIn);
	
	public default void increaseAmount(double amountChange){
		setAmount(getAmount() + amountChange);
	}
}

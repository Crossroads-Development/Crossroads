package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nonnull;

public interface IReagent{

	@Nonnull
	public IReagentType getType();

	@Nonnull
	public MatterPhase getPhase();

	public void updatePhase(boolean polar, boolean nonPolar, boolean aquaRegia);

	public double getTemp();

	public void setTemp(double tempIn);

	public double getAmount();

	public void setAmount(double amountIn);

	/**
	 * @param amountChange
	 * @return The new amount. 
	 */
	public default double increaseAmount(double amountChange, double newMatTemp){
		double oldAmount = getAmount();
		if(amountChange > 0 ){
			setTemp(((getTemp() * oldAmount) + (amountChange * newMatTemp)) / (oldAmount + amountChange));
		}
		setAmount(oldAmount + amountChange);
		return getAmount();
	}
}

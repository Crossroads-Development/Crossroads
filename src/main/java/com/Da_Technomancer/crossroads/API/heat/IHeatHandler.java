package com.Da_Technomancer.crossroads.API.heat;

public interface IHeatHandler {
	
	public double getTemp();
	
	public void setTemp(double tempIn);
	
	public void addHeat(double heat);
}

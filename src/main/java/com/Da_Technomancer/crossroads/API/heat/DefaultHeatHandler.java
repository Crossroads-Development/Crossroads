package com.Da_Technomancer.crossroads.API.heat;

public class DefaultHeatHandler implements IHeatHandler{
	
	private double temp;
	
	@Override
	public double getTemp() {
		return temp;
	}

	@Override
	public void setTemp(double tempIn) {
		temp = tempIn;
	}

	@Override
	public void addHeat(double heat) {
		temp += heat;
	}

}

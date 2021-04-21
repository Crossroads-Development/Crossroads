package com.Da_Technomancer.crossroads.API.heat;

/**Specifications for the heat system require that nothing that decreases its temperature can go below -273*C.*/
public interface IHeatHandler{

	public double getTemp();

	public void setTemp(double tempIn);

	public void addHeat(double heat);
	
}

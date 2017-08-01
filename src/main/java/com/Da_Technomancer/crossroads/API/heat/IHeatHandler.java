package com.Da_Technomancer.crossroads.API.heat;

import java.util.HashMap;

import com.Da_Technomancer.crossroads.API.enums.CableThemes;

/**Specifications for the heat system require that nothing that decreases its temperature can go below -273*C.*/
public interface IHeatHandler{
	
	public static final HashMap<String, CableThemes> OREDICT_TO_THEME = new HashMap<String, CableThemes>();
	
	public double getTemp();

	public void setTemp(double tempIn);

	public void addHeat(double heat);
	
}

package com.Da_Technomancer.crossroads.API.heat;

public class HeatUtil{

	public static final double ABSOLUTE_ZERO = -273D;

	public static double toKelvin(double celcius){
		return celcius + ABSOLUTE_ZERO;
	}

	public static double toCelcius(double kelvin){
		return kelvin - ABSOLUTE_ZERO;
	}
}

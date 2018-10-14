package com.Da_Technomancer.crossroads.API.heat;

public class HeatUtil{

	public static final double ABSOLUTE_ZERO = -273D;

	public static double toKelvin(double celcius){
		return celcius + ABSOLUTE_ZERO;
	}

	public static double toCelcius(double kelvin){
		return kelvin - ABSOLUTE_ZERO;
	}

	/**
	 * When provided with an array of temperatures in ascending order, finds the index of the highest lower bound of the temperature, or -1 if no such bound exists.
	 * Used to find the operating speed/heat usage of many heat machines based on temperature
	 * @param temp The temperature
	 * @param tempTiers The array of temperatures in ascending order. Must be in the same units (celcius or kelvin) as temp
	 * @return The index of the highest lower bound, or -1 if no such bound is in tempTiers
	 */
	public static int getHeatTier(double temp, int[] tempTiers){
		for(int i = tempTiers.length - 1; i >= 0; i--){
			if(temp >= tempTiers[i]){
				return i;
			}
		}
		return -1;
	}
}

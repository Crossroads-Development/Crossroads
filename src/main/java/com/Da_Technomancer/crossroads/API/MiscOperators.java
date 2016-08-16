package com.Da_Technomancer.crossroads.API;

//This class is for holding operations that I use often and aren't built into java.
public final class MiscOperators{

	public static double betterRound(double numIn, int decPlac){

		double opOn = Math.round(numIn * Math.pow(10, decPlac)) / Math.pow(10D, decPlac);
		return opOn;
	}

	/**
	 * a version of Math.ceil that factors in negative values better. Instead of
	 * hitting ints, it uses the secong arg ex. tiers = 1 is like ceil, tiers =
	 * 2 means goes to closest .5 value, rounding up
	 */
	public static double centerCeil(double numIn, int tiers){
		return ((numIn > 0) ? Math.ceil(numIn * tiers) : Math.floor(numIn * tiers)) / tiers;
	}

	public static double findEfficiency(double speedIn, double lowerLimit, double upperLimit){
		speedIn = Math.abs(speedIn);
		return speedIn < lowerLimit ? 0 : (speedIn >= upperLimit ? 1 : (speedIn - lowerLimit) / (upperLimit - lowerLimit));
	}

	public static int posOrNeg(int in){
		return in == 0 ? 0 : (in < 0 ? -1 : 1);
	}

	public static double posOrNeg(double in){
		return in == 0 ? 0 : (in < 0 ? -1D : 1D);
	}
}

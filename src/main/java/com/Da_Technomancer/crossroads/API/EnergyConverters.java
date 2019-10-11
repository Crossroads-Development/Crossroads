package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.crossroads.CRConfig;

public final class EnergyConverters{

	/**
	 * The number of mB of liquid fat that is equivalent to 1 food
	 * value (value = hunger restored + saturation restored.
	 */
	public static final int FAT_PER_VALUE = 100;

	/**
	 * @return The number of degrees one bucket of steam is worth
	 */
	public static double degPerSteamBucket(){
		return CRConfig.steamWorth.get();
	}

	/**
	 * @return The numbers of degrees one Joule (Rotary energy) is worth
	 */
	public static double degPerJoule(){
		return 1D / (double) CRConfig.jouleWorth.get();
	}

	/**
	 * The number of mB of molten metal in one ingot
	 */
	public static final int INGOT_MB = 144;
}

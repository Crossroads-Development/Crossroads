package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.crossroads.ModConfig;

public final class EnergyConverters{

	/**
	 * The number of mB of liquid fat that is equivalent to 1 food
	 * value (value = hunger restored + saturation restored.
	 */
	public static final int FAT_PER_VALUE = 100;

	/**
	 * The number of mB of molten copshowium produced from 1mb of molten copper OR 1mb of distilled water.
	 * Based on balance and convenience.
	 */
	public static final double COPSHOWIUM_PER_COPPER = 1.8D;

	/**
	 * Conversion factor between degrees kelvin normal heat, and degrees kelvin * amount alchemy system. Based on game balance.
	 */
	public static final double ALCHEMY_TEMP_CONVERSION = .01D;

	/**
	 * @return The number of degrees one bucket of steam is worth
	 */
	public static double degPerSteamBucket(boolean client){
		return ModConfig.getConfigDouble(ModConfig.steamWorth, client);
	}

	/**
	 * @return The numbers of degrees one Joule (Rotary energy) is worth
	 */
	public static double degPerJoule(boolean client){
		return 1D / ModConfig.getConfigDouble(ModConfig.jouleWorth, client);
	}

	/**
	 * The number of mB of molten metal in one ingot
	 */
	public static final int INGOT_MB = 144;
}

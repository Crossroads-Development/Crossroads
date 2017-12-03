package com.Da_Technomancer.crossroads.API;

public final class EnergyConverters{

	public static double convertBiomeTemp(double rawTemp){
		return rawTemp * 17.5D - 2.5D;
	}

	/**
	 * The number of degrees that 1000mB of steam is worth. Based on game
	 * balance.
	 */
	public static final double DEG_PER_BUCKET_STEAM = 50D;

	/**
	 * The number of degrees (thermal energy) that one joule (rotary energy) is
	 * worth. Based on simplicity & game balance. Scientifically speaking, this
	 * conversion is far too simple (Heat capacity, thermodynamics, units,
	 * etc.)
	 */
	public static final double DEG_PER_JOULE = 1D;

	/**
	 * The number of mB of liquid fat that is equivalent to 1 food
	 * value (value = hunger restored + saturation restored.
	 */
	public static final int FAT_PER_VALUE = 100;
	
	/**
	 * The RATE up equivalent to 1 speed(Fields). 
	 */
	public static final double RATE_PER_SPEED = 16;
	
	/**
	 * The gear speed equivalent to 1 FLUX up (Fields). 
	 * Value derived from 1 revolution = 32 FLUX, if speed were maintained for 1 second. 
	 */
	public static final double SPEED_PER_FLUX = Math.PI / 16D;
	
	/**
	 * The number of mB of molten copshowium produced from 1mb of molten copper OR 1mb of distilled water.
	 * Based on balance and convenience. 
	 */
	public static final double COPSHOWIUM_PER_COPPER = 1.8D;
	
	/**
	 * Conversion factor between degrees kelvin normal heat, and degrees kelvin * amount alchemy system. Based on steam cost. 
	 */
	public static final double ALCHEMY_TEMP_CONVERSION = .025D;
}

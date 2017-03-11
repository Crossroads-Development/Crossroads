package com.Da_Technomancer.crossroads.API;

public final class EnergyConverters{

	/**
	 * A multiplier for biome temp. to get degrees. Based on reality.
	 */
	public static final float BIOME_TEMP_MULT = 30F;

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
	public static final double DEG_PER_JOULE = 4D;

	/**
	 * The number of mB of liquid fat that is equivalent to 1 food
	 * value (value = hunger restored + saturation restored.
	 */
	public static final int FAT_PER_VALUE = 100;
	
	/**
	 * The gear speed equivalent to 1 RATE up (Fields). 
	 */
	public static final double SPEED_PER_RATE = 1D / 16D;
	

	/**
	 * The gear speed equivalent to 1 FLUX up (Fields). 
	 * The Rate Manipulator uses Rotations per FLUX, so it first divides this value by time (1 tick = 1/20 second).
	 * Value derived from 1 revolution = 32 FLUX.
	 */
	public static final double SPEED_PER_FLUX = Math.PI / 16D;
	
	/**
	 * The number of mB of molten copshowium produced from 1mb of molten copper OR 1mb of distilled water.
	 * Based on balance and convenientce. 
	 */
	public static final double COPSHOWIUM_PER_COPPER = 1.8D;
}

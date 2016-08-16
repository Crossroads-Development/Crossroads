package com.Da_Technomancer.crossroads.API;

public final class EnergyConverters {
	
	/**
	 * A multiplier for biome temp. to get degrees. Based on reality.
	 */
	public static final float BIOME_TEMP_MULT = 30F;
	
	/**
	 * The number of degrees one bucket of steam is worth. Based on game balance.
	 */
	public static final double DEG_PER_BUCKET_STEAM = 50D;
	
	/**
	 * The number of degrees (thermal energy) that one joule (rotary energy) is worth. 
	 * Based on simplicity & game balance. 
	 * Scientifically speaking, this conversion is too simple (Heat capacity, thermodynamics, dimensions, etc.)
	 */
	public static final double DEG_PER_JOULE = 4D;
	
	/**The number of millibuckets of liquid fat that is equivalent to 1 food value (value = hunger restored + saturation restored.
	 */
	public static final int FAT_PER_VALUE = 100;
	
	/** NYI.
	 * Based on game balance
	 */
	public static final double RF_PER_JOULE = 30D;
}

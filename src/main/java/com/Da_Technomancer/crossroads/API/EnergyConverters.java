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
	
	/*
	 * The number of degrees (thermal energy) that one joule (rotary energy) is worth. 
	 * Based on simplicity & game balance. 
	 * Scientifically speaking, this conversion is too simple (Heat capacity, thermodynamics, dimensions, etc.)
	 */
	public static final double DEG_PER_JOULE = 4D;
	
	/* TODO
	 * Based on game balance
	 */
	public static final double RF_PER_JOULE = 30D;
}

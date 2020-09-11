package com.Da_Technomancer.crossroads.API.heat;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.List;

public class HeatUtil{

	/**
	 * Absolute zero in degrees C
	 */
	public static final double ABSOLUTE_ZERO = -273D;

	/**
	 * Suggested maximum temperature value. Not required to enforce a maximum temperature- but if doing so to avoid overflows, this value should be the upper bound
	 */
	public static final double MAX_TEMP = 50_000;

	public static double toKelvin(double celcius){
		return celcius - ABSOLUTE_ZERO;
	}

	public static double toCelcius(double kelvin){
		return kelvin + ABSOLUTE_ZERO;
	}

	/**
	 * Adds heat information to a tooltip/readout
	 * @param chat The chat list. One line per entry, will be modified
	 * @param temp The temperature, in degrees C
	 * @param biomeTemp The biome temperature, in degrees C. Specify a value below absolute zero to not print this
	 */
	public static void addHeatInfo(List<ITextComponent> chat, double temp, double biomeTemp){
		chat.add(new TranslationTextComponent("tt.crossroads.boilerplate.temp_k", CRConfig.formatVal(temp), CRConfig.formatVal(toKelvin(temp))));
		if(biomeTemp >= ABSOLUTE_ZERO){
			chat.add(new TranslationTextComponent("tt.crossroads.boilerplate.temp.biome", CRConfig.formatVal(biomeTemp)));
		}
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

	/**
	 * Calculates the biome temperature at a location
	 * @param world The world (client or server)
	 * @param pos The position to find the temperature at
	 * @return The biome temperature, in degrees C
	 */
	public static double convertBiomeTemp(World world, BlockPos pos){
		if(world == null || pos == null){
			return ABSOLUTE_ZERO;
		}
		Biome biome = world.getBiome(pos);
		double rawTemp = biome.getTemperature(pos);
		//This formula was derived with the power of wikipedia and excel spreadsheets to compare biome temperatures to actual real world temperatures.
		//Most people probably wouldn't care if I'd just pulled it out of my *rse, but I made an effort and I want someone to know this. Appreciate it. Please?
		double outTemp = rawTemp * 17.5D - 2.5D;
		if(biome.getCategory() == Biome.Category.NETHER){
			outTemp = Math.max(outTemp, CRConfig.hellTemperature.get());
		}
		return MiscUtil.preciseRound(outTemp, 3);
	}
}

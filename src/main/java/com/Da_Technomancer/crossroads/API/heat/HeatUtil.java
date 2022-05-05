package com.Da_Technomancer.crossroads.API.heat;

import com.Da_Technomancer.crossroads.API.CRReflection;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.ReflectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class HeatUtil{

	private static final Method GET_BIOME_TEMPERATURE_NO_CACHE = ReflectionUtil.reflectMethod(CRReflection.BIOME_TEMPERATURE_NO_CACHE);

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
	public static void addHeatInfo(List<Component> chat, double temp, double biomeTemp){
		chat.add(new TranslatableComponent("tt.crossroads.boilerplate.temp_k", CRConfig.formatVal(temp), CRConfig.formatVal(toKelvin(temp))));
		if(biomeTemp >= ABSOLUTE_ZERO){
			chat.add(new TranslatableComponent("tt.crossroads.boilerplate.temp.biome", CRConfig.formatVal(biomeTemp)));
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
	 * Cache the result if possible
	 * @param world The world (client or server)
	 * @param pos The position to find the temperature at
	 * @return The biome temperature, in degrees C
	 */
	public static double convertBiomeTemp(Level world, BlockPos pos){
		if(world == null || pos == null){
			return ABSOLUTE_ZERO;
		}
		Holder<Biome> biomeHolder = world.getBiome(pos);
		Biome biome = biomeHolder.value();
		double rawTemp;
		if(GET_BIOME_TEMPERATURE_NO_CACHE != null){
			try{
				rawTemp = (float) GET_BIOME_TEMPERATURE_NO_CACHE.invoke(biome, pos);
			}catch(IllegalAccessException | InvocationTargetException | ClassCastException e){
				Crossroads.logger.catching(e);
				rawTemp = biome.getBaseTemperature();
			}
		}else{
			rawTemp = biome.getBaseTemperature();
		}

		//This formula was derived with the power of wikipedia and excel spreadsheets to compare biome temperatures to actual real world temperatures.
		//Most people probably wouldn't care if I'd just pulled it out of my *rse, but I made an effort and I want someone to know this. Appreciate it. Please?
		double outTemp = rawTemp * 16D - 3D;
		if(Biome.getBiomeCategory(biomeHolder) == Biome.BiomeCategory.NETHER){
			outTemp = Math.max(outTemp, CRConfig.hellTemperature.get());
		}
		return MiscUtil.preciseRound(outTemp, 3);
	}
}

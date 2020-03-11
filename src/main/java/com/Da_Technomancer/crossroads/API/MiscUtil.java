package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.essentials.ESConfig;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

public final class MiscUtil{

	/**
	 * A common style applied to "quip" lines in tooltips
	 */
	public static final Style TT_QUIP = ESConfig.TT_QUIP;

	/**
	 * Rounds to a set number of decimal places
	 * @param numIn The value to round
	 * @param decPlac The number of decimal places to round to
	 * @return The rounded value
	 */
	public static double preciseRound(double numIn, int decPlac){
		return Math.round(numIn * Math.pow(10, decPlac)) / Math.pow(10D, decPlac);
	}

	/**
	 * The same as Math.round except if the decimal
	 * is exactly .5 then it rounds down.
	 * 
	 * This is for systems that require rounding and
	 * NEED the distribution of output to not be higher than
	 * the input to prevent dupe bugs.
	 * @param in The value to round
	 * @return The rounded value
	 */
	public static int safeRound(double in){
		if(in % 1 <= .5D){
			return (int) Math.floor(in);
		}else{
			return (int) Math.ceil(in);
		}
	}

	/**
	 * A server-side friendly version of Entity.class' raytrace (currently called Entity#func_213324_a(double, float, boolean))
	 */
	public static BlockRayTraceResult rayTrace(Entity ent, double blockReachDistance){
		Vec3d vec3d = ent.getPositionVector().add(0, ent.getEyeHeight(), 0);
		Vec3d vec3d2 = vec3d.add(ent.getLook(1F).scale(blockReachDistance));
		return ent.world.rayTraceBlocks(new RayTraceContext(vec3d, vec3d2, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, ent));
	}

	/**
	 * Localizes the input. Do not trust the result if called on the physical server
	 * @param input The string to localize
	 * @return The localized string
	 */
	public static String localize(String input){
		return new TranslationTextComponent(input).getUnformattedComponentText();
	}

	/**
	 * Localizes and formats the input. Do not trust the result if called on the physical server
	 * @param input The string to localize
	 * @param formatArgs Arguments to pass the formatter
	 * @return The localized and formatted string
	 */
	public static String localize(String input, Object... formatArgs){
		return new TranslationTextComponent(input, formatArgs).getFormattedText();
	}

	public static String getLocalizedFluidName(String localizationKey){
		return localizationKey == null || localizationKey.isEmpty() || localizationKey.equals("block.minecraft.air") ? localize("tt.crossroads.boilerplate.empty") : localize(localizationKey);
	}
}

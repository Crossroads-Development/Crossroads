package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.essentials.ESConfig;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;

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

	/**
	 * Calculates a set of integer quantities to withdraw from a fixed integer source, where the total size of the withdrawn set is toWithdraw
	 * Does not mutate the passed arguments
	 * Attempts to distribute proportionally to source, with an over-emphasis on small amounts for the remainder
	 * Will not withdraw any variety in greater quantity than in source
	 * @param src The source quantities to calculate withdrawal from. Only positive and zero values are allowed
	 * @param toWithdraw The quantity to withdraw. Must be positive or zero
	 * @return The distribution withdrawn. Each index corresponds to the same src index
	 */
	public static int[] withdrawExact(int[] src, int toWithdraw){
		if(toWithdraw <= 0 || src.length == 0){
			//Withdraw nothing
			return new int[src.length];
		}
		int srcQty = 0;
		for(int val : src){
			srcQty += val;
		}
		if(toWithdraw < srcQty){
			int[] withdrawn = new int[src.length];
			double basePortion = (double) toWithdraw / (double) srcQty;//Multiplier for src applied get withdrawn. Any remaining space will be filled by a different distribution
			int totalWithdrawn = 0;
			for(int i = 0; i < withdrawn.length; i++){
				int toMove = (int) (basePortion * src[i]);//Intentional truncation- rounding down
				totalWithdrawn += toMove;
				withdrawn[i] += toMove;
			}

			if(totalWithdrawn < toWithdraw){
				//For the remaining space to fill, perform a round robin distribution
				//We start the distribution with the smallest (least magnitude pointed to) index, and end with the largest.
				//We do this to reduce dependence on the index order, and because it has been found that ensuring small pools are drawn from is more conveniently for play

				//Sort the source pools from least to greatest
				//Selection sort, using an array of values where each value is an index in src
				int[] sorted = new int[src.length];
				for(int i = 0; i < sorted.length - 1; i++){
					int smallestVal = Integer.MAX_VALUE;
					int smallestInd = 0;
					for(int j = i; j < sorted.length; j++){
						if(i == 0){
							//For the first pass, we populate sorted with 0,1,2,3,etc to represent the original, unsorted array
							sorted[j] = j;
						}
						//src[sorted[j]] < smallestVal is the normal sorting ordering
						//sorted[j] < sorted[smallestInd] is a secondary sorting ordering that is only considered if (src[sorted[j]] == smallestVal)
						//This secondary sorting ordering was added to maintain certain useful specialized behaviour from the previous algorithm (red-green-blue-black sorting). It may be removed in a later update
						//It does not interfere with sorting from lowest to highest quantity, and only acts as a tie-breaker
						if(src[sorted[j]] < smallestVal || sorted[j] < sorted[smallestInd] && src[sorted[j]] == smallestVal){
							smallestInd = sorted[j];
							smallestVal = src[sorted[j]];
						}
					}
					if(smallestInd != i){
						int toSwap = sorted[i];
						sorted[i] = smallestInd;
						sorted[smallestInd] = toSwap;
					}
				}

				//Perform round robin distribution
				int indexInSorted = 0;//The index in sorted (which contains more indices...) we are drawing from
				while(totalWithdrawn < toWithdraw){
					int indexInSrc = sorted[indexInSorted];
					if(src[indexInSrc] > withdrawn[indexInSrc]){//Make sure there is sufficient to withdraw from this pool, otherwise proceed to the next pool
						//Withdraw one from the current pool, and move to the next pool in sorted
						withdrawn[indexInSrc]++;
						totalWithdrawn++;
						indexInSorted++;
					}else{
						//Proceed to next index without withdrawing
						indexInSorted++;
						indexInSorted %= sorted.length;
					}
				}
			}

			return withdrawn;
		}else{
			//Less total in src than to withdraw. Return same as in src
			return Arrays.copyOf(src, src.length);
		}
	}
}

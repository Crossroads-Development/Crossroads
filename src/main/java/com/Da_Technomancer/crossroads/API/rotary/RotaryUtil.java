package com.Da_Technomancer.crossroads.API.rotary;

import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.essentials.shared.ISlaveAxisHandler;
import net.minecraft.util.EnumFacing;

public class RotaryUtil{

	/**
	 * This method should be called BEFORE adding an ISlaveAxisHandler to the stored list.
	 *
	 * @param axis An ISlaveAxisHandler of the IAxisHandler calling this method.
	 * @param toAdd The ISlaveAxisHandler found during propagation
	 * @return true if toAdd contains axis, even if nested. If true, the calling IAxisHandler should self-destruct (or otherwise suspend operation).
	 * <p>
	 * It is possible for this method to throw a StackOverflow error. There are two possible causes of this: either there is an unreasonable amount of nesting going on,
	 * or there is a different infinite loop that should have been prevented at an earlier point. The disableSlaves config can be used to rescue a world in either of these cases.
	 */
	public static boolean contains(ISlaveAxisHandler axis, ISlaveAxisHandler toAdd){
		if(ModConfig.disableSlaves.getBoolean() || toAdd == axis){
			return true;
		}
		if(toAdd.getContainedAxes().isEmpty()){
			return false;
		}
		for(ISlaveAxisHandler inner : toAdd.getContainedAxes()){
			if(contains(axis, inner)){
				return true;
			}
		}
		return false;
	}

	public static double getDirSign(EnumFacing oldGearFacing, EnumFacing newGearFacing){
		return -oldGearFacing.getAxisDirection().getOffset() * newGearFacing.getAxisDirection().getOffset();
	}

	public static double findEfficiency(double speedIn, double lowerLimit, double upperLimit){
		speedIn = Math.abs(speedIn);
		return speedIn < lowerLimit ? 0 : (speedIn >= upperLimit ? 1 : (speedIn - lowerLimit) / (upperLimit - lowerLimit));
	}

	public static double posOrNeg(double in, double zeroCase){
		return in == 0 ? zeroCase : (in < 0 ? -1 : 1);
	}
}

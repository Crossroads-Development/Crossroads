package com.Da_Technomancer.crossroads.API.rotary;

import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.essentials.shared.IAxleHandler;
import com.Da_Technomancer.essentials.shared.ISlaveAxisHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

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

	/**
	 * Returns the total energy, adjusted for energy loss, of the passed IAxleHandlers
	 * @param axles A list of IAxleHandlers to have their energies summed and adjusted
	 * @return The total energy adjusted for energy loss
	 */
	public static double getTotalEnergy(List<IAxleHandler> axles){
		double sumEnergy  = 0;
		double sumInertia = 0;
		double sumIW = 0;

		for(IAxleHandler axle : axles){
			if(axle == null){
				continue;
			}
			sumEnergy += axle.getMotionData()[1] * Math.signum(axle.getRotationRatio());
			sumInertia += axle.getMoInertia();
			sumIW += axle.getMoInertia() * Math.abs(axle.getMotionData()[0]);
		}

		sumEnergy = Math.signum(sumEnergy) * Math.max(0, Math.abs(sumEnergy) - ModConfig.getConfigDouble(ModConfig.rotaryLoss, false) * Math.pow(sumIW / sumInertia, 2));
		return sumEnergy;
	}

	/** I keep changing my mind about how to determine whether gears can connect diagonally through a block.
	 * Implementers of IAxleHandler should use this to determine whether they can connect diagonally through a block.
	 * @param world The World.
	 * @param pos The BlockPos of the block space that is being connected through.
	 * @param fromDir The direction from pos that the caller is located.
	 * @param toDir The direction from pos that the end point of the connection is located.
	 * @return Whether a connection is allowed. Does not verify that the start/endpoints are valid.
	 */
	public static boolean canConnectThrough(World world, BlockPos pos, EnumFacing fromDir, EnumFacing toDir){
		IBlockState state = world.getBlockState(pos);
		return !state.getBlock().isNormalCube(state, world, pos);
	}
}

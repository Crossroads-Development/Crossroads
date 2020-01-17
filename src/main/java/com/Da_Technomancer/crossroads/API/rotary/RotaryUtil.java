package com.Da_Technomancer.crossroads.API.rotary;

import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.SendMasterKeyToClient;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.rotary.LargeGearMaster;
import com.Da_Technomancer.crossroads.blocks.rotary.LargeGearSlave;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;

import java.util.List;

public class RotaryUtil{

	private static int masterKey = 1;

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
		if(CRConfig.disableSlaves.get() || toAdd == axis){
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

	public static double getDirSign(Direction oldGearFacing, Direction newGearFacing){
		return -oldGearFacing.getAxisDirection().getOffset() * newGearFacing.getAxisDirection().getOffset();
	}

	public static double findEfficiency(double speedIn, double lowerLimit, double upperLimit){
		speedIn = Math.abs(speedIn);
		return speedIn < lowerLimit ? 0 : (speedIn >= upperLimit ? 1 : (speedIn - lowerLimit) / (upperLimit - lowerLimit));
	}

	public static double posOrNeg(double in, double zeroCase){
		return in == 0D || in == -0D ? zeroCase : Math.signum(in);
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

		sumEnergy = Math.signum(sumEnergy) * Math.max(0, Math.abs(sumEnergy) - CRConfig.rotaryLoss.get() * Math.pow(sumIW / sumInertia, 2));
		return sumEnergy;
	}

	/**
	 * I keep changing my mind about how to determine whether gears can connect diagonally through a block.
	 * Implementers of IAxleHandler should use this to determine whether they can connect diagonally through a block.
	 * @param world The World.
	 * @param pos The BlockPos of the block space that is being connected through.
	 * @param fromDir The direction from pos that the caller is located.
	 * @param toDir The direction from pos that the end point of the connection is located.
	 * @return Whether a connection is allowed. Does not verify that the start/endpoints are valid.
	 */
	public static boolean canConnectThrough(World world, BlockPos pos, Direction fromDir, Direction toDir){
		BlockState state = world.getBlockState(pos);
		return !state.isNormalCube(world, pos) && state.getBlock() != CRBlocks.largeGearSlave && state.getBlock() != CRBlocks.largeGearMaster;
	}

	/**
	 * Returns whether the block at the provided position is solid to gears on the specified side
	 * @param world The World
	 * @param pos The block's position
	 * @param side The side the gear will be placed against
	 * @return Whether it should be solid to small gears
	 */
	public static boolean solidToGears(World world, BlockPos pos, Direction side){
		//The current definition of "solid":
		//Block collision shape contains the 2x2 of pixels in the center of the face in side
		//And block is not a large gear
		BlockState state = world.getBlockState(pos);
		if(state.getBlock() instanceof LargeGearSlave || state.getBlock() instanceof LargeGearMaster){
			return false;
		}
		VoxelShape shape = state.getCollisionShape(world, pos);
		Direction.Axis axis = side.getAxis();
		shape = shape.project(side);//Eliminate all voxels that don't touch the side of interest, and extend remaining voxels
		final boolean[] passed = new boolean[1];
		final double rad = 1;
		//Known issue: If all necessary components of the voxel exist, but are spread over multiple voxels, this will fail because we only check one voxelbox at a time
		//However, that is a very rare edge case (if you find it in vanilla, let me know and I'll special case it), and checking for it increases complexity from O(n) to- I'm not actually sure, I'm not a CS major, but probably O(n^2) or something
		shape.forEachBox((xSt, ySt, zSt, xEn, yEn, zEn) -> {
			//Only continue checking if we haven't found a match
			if(!passed[0]){
				//Known: Each box will extend from 0 to 16 on the axis of interest (due to project call)- the axis check is therefore redundant, but lets us speed things up
				if((axis == Direction.Axis.X || xSt <= 8D - rad && xEn >= 8D + rad) && (axis == Direction.Axis.Y || ySt <= 8D - rad && yEn >= 8D + rad) && (axis == Direction.Axis.Z || zSt <= 8D - rad && zEn >= 8D + rad)){
					passed[0] = true;
				}
			}
		});
		return passed[0];
	}

	/**
	 * Increases the masterKey by one
	 * @param sendPacket If true, sends a packet to the client forcing the masterKey to increase
	 */
	public static void increaseMasterKey(boolean sendPacket){
		masterKey++;
		if(sendPacket){
			CrossroadsPackets.sendPacketToAll(new SendMasterKeyToClient(masterKey));
		}
	}

	public static int getMasterKey(){
		return masterKey;
	}

	public static void setMasterKey(int masterKey){
		RotaryUtil.masterKey = masterKey;
	}
}

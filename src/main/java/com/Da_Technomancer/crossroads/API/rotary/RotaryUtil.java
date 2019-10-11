package com.Da_Technomancer.crossroads.API.rotary;

import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.SendMasterKeyToClient;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import net.minecraft.block.Block;
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
		return in == 0 ? zeroCase : Math.signum(in);
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
		return !state.getBlock().isNormalCube(state, world, pos) && state.getBlock() != CrossroadsBlocks.largeGearSlave && state.getBlock() != CrossroadsBlocks.largeGearMaster;
	}

	/**
	 * Returns whether the block at the provided position is solid to gears on the specified side
	 * @param world The World
	 * @param pos The block's position
	 * @param side The side the gear will be placed against
	 * @return Whether it should be solid to small gears
	 */
	public static boolean solidToGears(World world, BlockPos pos, Direction side){
		VoxelShape shape = world.getBlockState(pos).getShape(world, pos);
		//TODO THIS IS A PLACEHOLDER and does not work
		//This currently works for solid surfaces, but not things like the ends of axles (probably- best to test this)
		return Block.func_220055_a(world, pos, side);//This method is also used by torches
//		BlockFaceShape shape = world.getBlockState(pos).getBlockFaceShape(world, pos, side);
//		return world.isSideSolid(pos, side, false) || shape == BlockFaceShape.SOLID || shape == BlockFaceShape.CENTER || shape == BlockFaceShape.CENTER_BIG || shape == BlockFaceShape.CENTER_SMALL;
	}

	/**
	 * Increases the masterKey by one
	 * @param sendPacket If true, sends a packet to the client forcing the masterKey to increase
	 */
	public static void increaseMasterKey(boolean sendPacket){
		masterKey++;
		if(sendPacket){
//			CrossroadsPackets.network.sendToAll(new SendMasterKeyToClient(masterKey));
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

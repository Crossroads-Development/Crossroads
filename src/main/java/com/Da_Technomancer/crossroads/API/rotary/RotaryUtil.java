package com.Da_Technomancer.crossroads.API.rotary;

import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.packets.SendMasterKeyToClient;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.rotary.LargeGearMaster;
import com.Da_Technomancer.crossroads.blocks.rotary.LargeGearSlave;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class RotaryUtil{

	/**
	 * The masterKey is a way of tracking when Master Axes should regenerate/recheck their networks
	 * Master axes are allowed to ignore this, but it allows for significant optimizations by reducing unnecessary checks, as it will be incremented every time a gear/component is broken/updated
	 */
	private static int masterKey = 1;

	/**
	 * Adds information about an axle handler to chat/tooltip
	 * @param chat The text list. One entry per line, will be modified
	 * @param motData The motion data of the axle (speed, energy, power- any further args ignored)
	 * @param inertia The moment of inertia
	 * @param rotRatio The rotation ratio
	 * @param compact Whether to compact the output into one line of chat
	 */
	public static void addRotaryInfo(List<ITextComponent> chat, double[] motData, double inertia, double rotRatio, boolean compact){
		if(compact){
			//Print speed, energy, power, inertia, and rot ratio
			chat.add(new TranslationTextComponent("tt.crossroads.boilerplate.rotary.compact", CRConfig.formatVal(motData[0]), CRConfig.formatVal(motData[1]), CRConfig.formatVal(motData[2]), CRConfig.formatVal(inertia), CRConfig.formatVal(rotRatio)));
		}else{
			//Prints full data
			chat.add(new TranslationTextComponent("tt.crossroads.boilerplate.rotary.speed", CRConfig.formatVal(motData[0])));
			chat.add(new TranslationTextComponent("tt.crossroads.boilerplate.rotary.energy", CRConfig.formatVal(motData[1])));
			chat.add(new TranslationTextComponent("tt.crossroads.boilerplate.rotary.power", CRConfig.formatVal(motData[2])));
			chat.add(new TranslationTextComponent("tt.crossroads.boilerplate.rotary.setup", CRConfig.formatVal(inertia), CRConfig.formatVal(rotRatio)));
		}
	}

	public static double getDirSign(Direction oldGearFacing, Direction newGearFacing){
		return -getCCWSign(oldGearFacing) * getCCWSign(newGearFacing);
	}

	public static double findEfficiency(double speedIn, double lowerLimit, double upperLimit){
		speedIn = Math.abs(speedIn);
		return speedIn < lowerLimit ? 0 : (speedIn >= upperLimit ? 1 : (speedIn - lowerLimit) / (upperLimit - lowerLimit));
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

		if(sumInertia <= 0){
			return 0;
		}
		//Apply energy loss; based on average speed weighted by moment of inertia
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

	private static final VoxelShape GEAR_ANCHOR_SHAPE = Block.makeCuboidShape(7.05D, 7.05D, 7.05D, 8.95D, 8.95D, 8.95D);

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
		//And block is not the back of a large gear or leaves
		BlockState state = world.getBlockState(pos);
		if(state.getBlock() instanceof LargeGearSlave || (state.getBlock() instanceof LargeGearMaster && side != state.get(ESProperties.FACING).getOpposite())){
			return false;
		}
		if(state.isIn(BlockTags.LEAVES)){
			return false;//Vanilla convention has leaves as non-solid
		}

		//This is where the magic happens
		//Projections remove all cuboids that don't touch the passed side, and extend those that remain into a full column from one side to the opposite (the project method is poorly named)
		//Projections are cached by default, so this operation is fast
		//We have a reference anchor shape, which should fit neatly inside the projected shape if this is a solid surface
		return !VoxelShapes.compare(state.getCollisionShape(world, pos).project(side), GEAR_ANCHOR_SHAPE, IBooleanFunction.ONLY_SECOND);
	}

	/**
	 * Increases the masterKey by one
	 * @param sendPacket If true, sends a packet to the client forcing the masterKey to increase
	 */
	public static void increaseMasterKey(boolean sendPacket){
		masterKey++;
		if(sendPacket){
			CRPackets.sendPacketToAll(new SendMasterKeyToClient(masterKey));
		}
	}

	public static int getMasterKey(){
		return masterKey;
	}

	public static void setMasterKey(int masterKey){
		RotaryUtil.masterKey = masterKey;
	}

	/**
	 * Returns either 1 or -1, and represents the sign for rotation to be in the counter-clockwise direction
	 * @param dir The direction along the axis of rotation, with the direction being the 'front' of the axis
	 * @return The value to multiply the energy or speed by for CCW rotation
	 */
	public static double getCCWSign(Direction dir){
		return dir.getAxisDirection().getOffset();
	}
}

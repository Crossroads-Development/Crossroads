package com.Da_Technomancer.crossroads.API.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.packets.SendMasterKeyToClient;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.rotary.LargeGearMaster;
import com.Da_Technomancer.crossroads.blocks.rotary.LargeGearSlave;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
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
	 * @param axle The axle being added to the info chat. This method does nothing if null
	 * @param compact Whether to compact the output into one line of chat
	 */
	public static void addRotaryInfo(List<Component> chat, @Nullable IAxleHandler axle, boolean compact){
		if(axle == null){
			return;
		}
		if(compact){
			//Print speed, energy, power, inertia, and rot ratio
			chat.add(new TranslatableComponent("tt.crossroads.boilerplate.rotary.compact", CRConfig.formatVal(axle.getSpeed()), CRConfig.formatVal(axle.getEnergy()), CRConfig.formatVal(axle.getMoInertia()), CRConfig.formatVal(axle.getRotationRatio())));
		}else{
			//Prints full data
			double axleSpeed = axle.getSpeed();
			chat.add(new TranslatableComponent("tt.crossroads.boilerplate.rotary.speed", CRConfig.formatVal(axleSpeed), CRConfig.formatVal(axleSpeed * 60D / (Math.PI * 2D))));
			chat.add(new TranslatableComponent("tt.crossroads.boilerplate.rotary.energy", CRConfig.formatVal(axle.getEnergy())));
			chat.add(new TranslatableComponent("tt.crossroads.boilerplate.rotary.setup", CRConfig.formatVal(axle.getMoInertia()), CRConfig.formatVal(axle.getRotationRatio())));
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
	 * @param allowLoss Whether to perform energy loss
	 * @return A size 4 array, containing the total energy adjusted for energy loss, total energy change due to loss (0 if !allowLoss), resulting base system speed, and ΣI*R²
	 */
	public static double[] getTotalEnergy(List<IAxleHandler> axles, boolean allowLoss){
		double sumEnergy  = 0;
		double sumInertia = 0;
		double sumIW = 0;
		double sumIRot = 0;//I * R^2
		int lossMode = allowLoss ? CRConfig.rotaryLossMode.get() : 0;
		double lossCoeff = CRConfig.rotaryLoss.get();
		double lost = 0;

		for(IAxleHandler axle : axles){
			if(axle == null){
				continue;
			}

			//Tracks inertia of the system
			double moIntertia = axle.getMoInertia();
			double rotRatio = axle.getRotationRatio();
			sumInertia += moIntertia;
			sumIRot += moIntertia * Math.pow(rotRatio, 2);

			double axleEnergy = axle.getEnergy();
			double axleSpeed = axle.getSpeed();
			if(Double.isNaN(axleEnergy) || Double.isNaN(axleSpeed)){
				//One NaN value can corrupt an entire network, so we skip reading NaN values
				continue;
			}
			sumIW += moIntertia * Math.abs(axleSpeed);
			sumEnergy += axleEnergy * Math.signum(rotRatio);

			//Adds energy of the gear
			if(lossMode == 3){
				//Lose -(a*w) of gear energy each tick
				lost += Math.signum(axleSpeed) * axleSpeed * lossCoeff;
			}
		}

		if(sumInertia <= 0){
			//Totally zero mass systems must have 0 energy by definition
			return new double[4];
		}

		if(lossMode == 2){
			//Lose -(a%) of total energy each tick
			lost = sumEnergy * Math.max(lossCoeff / 100D, 0D);
		}else if(lossMode == 1){
			//Lose -(a * w^2) of energy each tick, where w is the I-weighted average speed of the entire system
			lost = Math.signum(sumEnergy) * lossCoeff * Math.pow(sumIW / sumInertia, 2);
		}

		if(Math.signum(sumEnergy) != Math.signum(sumEnergy - lost)){
			lost = sumEnergy;//Don't allow flipping sign from loss
		}

		sumEnergy -= lost;//Apply the loss

		double baseSpeed = Math.signum(sumEnergy) * Math.sqrt(Math.abs(sumEnergy) * 2D / sumIRot);

		return new double[] {sumEnergy, lost, baseSpeed, sumIRot};
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
	public static boolean canConnectThrough(Level world, BlockPos pos, Direction fromDir, Direction toDir){
		BlockState state = world.getBlockState(pos);
		return !state.isRedstoneConductor(world, pos) && state.getBlock() != CRBlocks.largeGearSlave && state.getBlock() != CRBlocks.largeGearMaster;
	}

	private static final VoxelShape GEAR_ANCHOR_SHAPE = Block.box(7.05D, 7.05D, 7.05D, 8.95D, 8.95D, 8.95D);

	/**
	 * Returns whether the block at the provided position is solid to gears on the specified side
	 * @param world The World
	 * @param pos The block's position
	 * @param side The side the gear will be placed against
	 * @return Whether it should be solid to small gears
	 */
	public static boolean solidToGears(Level world, BlockPos pos, Direction side){
		//The current definition of "solid":
		//Block collision shape contains the 2x2 of pixels in the center of the face in side
		//And block is not the back of a large gear or leaves
		BlockState state = world.getBlockState(pos);
		if(state.getBlock() instanceof LargeGearSlave || (state.getBlock() instanceof LargeGearMaster && side != state.getValue(ESProperties.FACING).getOpposite())){
			return false;
		}
		if(state.is(BlockTags.LEAVES)){
			return false;//Vanilla convention has leaves as non-solid
		}

		//This is where the magic happens
		//Projections remove all cuboids that don't touch the passed side, and extend those that remain into a full column from one side to the opposite (the project method is poorly named)
		//Projections are cached by default, so this operation is fast
		//We have a reference anchor shape, which should fit neatly inside the projected shape if this is a solid surface
		return !Shapes.joinIsNotEmpty(state.getCollisionShape(world, pos).getFaceShape(side), GEAR_ANCHOR_SHAPE, BooleanOp.ONLY_SECOND);
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
		return dir.getAxisDirection().getStep();
	}

	/**
	 * Connect axially to a tile entity
	 * Handles both IAxleHandler and IAxisHandler
	 * Does not connect via cog capability
	 * @param te The tile entity being connected to
	 * @param direction The side of the tile entity being connected to
	 * @param srcHandler The handler calling this
	 * @param master The master axis being propagated
	 * @param shouldRenderOffset Whether angles should be rendered with an offset
	 */
	public static void propagateAxially(@Nullable BlockEntity te, Direction direction, IAxleHandler srcHandler, IAxisHandler master, byte key, boolean shouldRenderOffset){
		if(te != null){
			LazyOptional<IAxisHandler> axisOpt = te.getCapability(Capabilities.AXIS_CAPABILITY, direction);
			if(axisOpt.isPresent()){
				axisOpt.orElseThrow(NullPointerException::new).trigger(master, key);
			}

			LazyOptional<IAxleHandler> axleOpt = te.getCapability(Capabilities.AXLE_CAPABILITY, direction);
			if(axleOpt.isPresent()){
				axleOpt.orElseThrow(NullPointerException::new).propagate(master, key, srcHandler.getRotationRatio(), 0, shouldRenderOffset);
			}
		}
	}
}

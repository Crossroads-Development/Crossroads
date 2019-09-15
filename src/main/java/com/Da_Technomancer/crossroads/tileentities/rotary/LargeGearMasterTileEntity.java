package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.packets.ILongReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLongToClient;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class LargeGearMasterTileEntity extends TileEntity implements ILongReceiver, ITickableTileEntity, IInfoTE{
	
	private GearFactory.GearMaterial type;
	private double[] motionData = new double[4];
	private double inertia = 0;
	private boolean borken = false;
	/**
	 * 0: angle, 1: clientW
	 */
	private float[] angleW = new float[2];
	private Direction facing = null;

	public Direction getFacing(){
		if(facing == null){
			BlockState state = world.getBlockState(pos);
			if(state.getBlock() != CrossroadsBlocks.largeGearMaster){
				return Direction.NORTH;
			}
			facing = state.get(EssentialsProperties.FACING);
		}
		return facing;
	}

	public boolean isRenderedOffset(){
		return handlerMain.renderOffset;
	}

	@Override
	public void addInfo(ArrayList<String> chat, PlayerEntity player, @Nullable Direction side, BlockRayTraceResult hit){
		chat.add("Speed: " + MiscUtil.betterRound(motionData[0], 3));
		chat.add("Energy: " + MiscUtil.betterRound(motionData[1], 3));
		chat.add("Power: " + MiscUtil.betterRound(motionData[2], 3));
		chat.add("I: " + inertia + ", Rotation Ratio: " + handlerMain.getRotationRatio());
	}

	public void initSetup(GearFactory.GearMaterial typ){
		type = typ;

		if(!world.isRemote){
			ModPackets.network.sendToAllAround(new SendLongToClient((byte) 1, type == null ? -1 : type.getIndex(), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}

		inertia = type == null ? 0 : MiscUtil.betterRound(type.getDensity() * 1.125D * 9D / 8D, 2);//1.125 because r*r/2 so 1.5*1.5/2
	}

	public GearFactory.GearMaterial getMember(){
		//The first material is returned instead of null to prevent edge case crashes.
		return type == null ? GearFactory.gearMats.get(0) : type;
	}

	private static final AxisAlignedBB RENDER_BOX = new AxisAlignedBB(-1, -1, -1, 2, 2, 2);

	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return RENDER_BOX.offset(pos);
	}

	public void breakGroup(Direction side, boolean drop){
		if(borken){
			return;
		}
		borken = true;
		for(int i = -1; i < 2; ++i){
			for(int j = -1; j < 2; ++j){
				world.setBlockToAir(pos.offset(side.getAxis() == Axis.X ? Direction.UP : Direction.EAST, i).offset(side.getAxis() == Axis.Z ? Direction.UP : Direction.NORTH, j));
			}
		}
		if(drop){
			world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(GearFactory.gearTypes.get(type).getLargeGear(), 1)));
		}
	}

	@Override
	public void update(){
		if(world.isRemote){
			angleW[0] += angleW[1] * 9D / Math.PI;
		}
	}

	@Override
	public void readFromNBT(CompoundNBT nbt){
		super.readFromNBT(nbt);

		// motionData
		for(int j = 0; j < 4; j++){
			motionData[j] = nbt.getDouble("[" + j + "]mot");
		}
		// member
		type = nbt.getInteger("type") < GearFactory.gearMats.size() ? GearFactory.gearMats.get(nbt.getInteger("type")) : GearFactory.gearMats.get(0);
		inertia = type == null ? 0 : MiscUtil.betterRound(type.getDensity() * 1.125D * 9D / 8D, 2);
		//1.125 because r*r/2 so 1.5*1.5/2

		angleW[0] = nbt.getFloat("angle");
		angleW[1] = nbt.getFloat("cl_w");
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT nbt){
		super.writeToNBT(nbt);

		// motionData
		for(int j = 0; j < 3; j++){
			if(motionData[j] != 0)
				nbt.setDouble("[" + j + "]mot", motionData[j]);
		}

		// member
		if(type != null){
			nbt.setInteger("type", type.getIndex());
		}

		nbt.setBoolean("new", true);
		nbt.setFloat("angle", angleW[0]);
		nbt.setFloat("cl_w", angleW[1]);
		return nbt;
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		if(type != null){
			nbt.setInteger("type", type.getIndex());
		}
		nbt.setBoolean("new", true);
		nbt.setFloat("angle", angleW[0]);
		nbt.setFloat("cl_w", angleW[1]);
		return nbt;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayerEntity sendingPlayer){
		if(identifier == 0){
			float angle = Float.intBitsToFloat((int) (message & 0xFFFFFFFFL));
			angleW[0] = Math.abs(angle - angleW[0]) > 5F ? angle : angleW[0];
			angleW[1] = Float.intBitsToFloat((int) (message >>> 32L));
		}else if(identifier == 1){
			type = message < 0 || message >= GearFactory.gearMats.size() ? GearFactory.gearMats.get(0) : GearFactory.gearMats.get((int) message);
		}else if(identifier == 2){
			handlerMain.renderOffset = message == 1;
		}
	}

	private final AxleHandler handlerMain = new AxleHandler();

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable Direction facing){
		if(capability == Capabilities.AXLE_CAPABILITY && (facing == null || facing.getAxis() == getFacing().getAxis())){
			return type != null;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == Capabilities.AXLE_CAPABILITY && (facing == null || facing.getAxis() == getFacing().getAxis())){
			return (T) handlerMain;
		}
		return super.getCapability(capability, facing);
	}

	private class AxleHandler implements IAxleHandler{

		private byte updateKey;
		private double rotRatio;
		private boolean renderOffset;
		private IAxisHandler axis;

		@Override
		public double[] getMotionData(){
			return motionData;
		}

		@Override
		public void propogate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius, boolean renderOffset){
			if(type == null){
				return;
			}

			if(lastRadius != 0){
				rotRatioIn *= lastRadius / 1.5D;
			}

			//If true, this has already been checked.
			if(key == updateKey){
				//If true, there is rotation conflict.
				if(rotRatio != rotRatioIn){
					masterIn.lock();
				}
				return;
			}

			if(masterIn.addToList(this)){
				return;
			}

			axis = masterIn;

			rotRatio = rotRatioIn;
			this.renderOffset = renderOffset;

			updateKey = key;

			Direction side = getFacing();
			
			for(int i = 0; i < 6; i++){
				if(i != side.getIndex() && i != side.getOpposite().getIndex()){
					Direction facing = Direction.byIndex(i);
					// Adjacent gears
					TileEntity adjTE = world.getTileEntity(pos.offset(facing, 2));
					if(adjTE != null){
						if(adjTE.hasCapability(Capabilities.COG_CAPABILITY, side)){
							adjTE.getCapability(Capabilities.COG_CAPABILITY, side).connect(masterIn, key, -rotRatio, 1.5D, facing.getOpposite(), renderOffset);
						}else if(adjTE.hasCapability(Capabilities.COG_CAPABILITY, facing.getOpposite())){
							//Check for large gears
							adjTE.getCapability(Capabilities.COG_CAPABILITY, facing.getOpposite()).connect(masterIn, key, RotaryUtil.getDirSign(side, facing) * rotRatio, 1.5D, side, renderOffset);
						}
					}

					// Diagonal gears
					TileEntity diagTE = world.getTileEntity(pos.offset(facing, 2).offset(side));
					if(diagTE != null && diagTE.hasCapability(Capabilities.COG_CAPABILITY, facing.getOpposite()) && RotaryUtil.canConnectThrough(world, pos.offset(facing, 2), facing.getOpposite(), side)){
						diagTE.getCapability(Capabilities.COG_CAPABILITY, facing.getOpposite()).connect(masterIn, key, -RotaryUtil.getDirSign(side, facing) * rotRatio, 1.5D, side.getOpposite(), renderOffset);
					}

					//Underside gears
					TileEntity undersideTE = world.getTileEntity(pos.offset(facing, 1).offset(side));
					if(undersideTE != null && undersideTE.hasCapability(Capabilities.COG_CAPABILITY, facing)){
						undersideTE.getCapability(Capabilities.COG_CAPABILITY, facing).connect(masterIn, key, -RotaryUtil.getDirSign(side, facing) * rotRatioIn, 1.5D, side.getOpposite(), renderOffset);
					}
				}
			}

			for(Direction.AxisDirection dir : Direction.AxisDirection.values()){
				Direction axleDir = dir == Direction.AxisDirection.POSITIVE ? getFacing() : getFacing().getOpposite();
				TileEntity connectTE = world.getTileEntity(pos.offset(axleDir));

				if(connectTE != null){
					if(connectTE.hasCapability(Capabilities.AXIS_CAPABILITY, axleDir.getOpposite())){
						connectTE.getCapability(Capabilities.AXIS_CAPABILITY, axleDir.getOpposite()).trigger(masterIn, key);
					}
					if(connectTE.hasCapability(Capabilities.SLAVE_AXIS_CAPABILITY, axleDir.getOpposite())){
						masterIn.addAxisToList(connectTE.getCapability(Capabilities.SLAVE_AXIS_CAPABILITY, axleDir.getOpposite()), axleDir.getOpposite());
					}

					if(connectTE.hasCapability(Capabilities.AXLE_CAPABILITY, axleDir.getOpposite())){
						connectTE.getCapability(Capabilities.AXLE_CAPABILITY, axleDir.getOpposite()).propogate(masterIn, key, rotRatio, 0, renderOffset);
					}
				}
			}
		}

		@Override
		public void disconnect(){
			axis = null;
		}

		@Override
		public double getMoInertia(){
			return inertia;
		}

		@Override
		public float getAngle(float partialTicks){
			return axis == null ? 0 : axis.getAngle(rotRatio, partialTicks, renderOffset, 7.5F);
		}

		@Override
		public double getRotationRatio(){
			return rotRatio;
		}

		@Override
		public void markChanged(){
			markDirty();
		}
		
		@Override
		public boolean shouldManageAngle(){
			return true;
		}
	}
}

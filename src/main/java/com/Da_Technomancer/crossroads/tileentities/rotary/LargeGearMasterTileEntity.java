package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.packets.ILongReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLongToClient;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class LargeGearMasterTileEntity extends TileEntity implements ILongReceiver, ITickable, IInfoTE{
	
	private GearFactory.GearMaterial type;
	private double[] motionData = new double[4];
	private double inertia = 0;
	private boolean borken = false;
	/**
	 * 0: angle, 1: clientW
	 */
	private float[] angleW = new float[2];
	private EnumFacing facing = null;

	public EnumFacing getFacing(){
		if(facing == null){
			IBlockState state = world.getBlockState(pos);
			if(state.getBlock() != ModBlocks.largeGearMaster){
				return EnumFacing.NORTH;
			}
			facing = state.getValue(EssentialsProperties.FACING);
		}
		return facing;
	}
	
	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
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

	public void breakGroup(EnumFacing side, boolean drop){
		if(borken){
			return;
		}
		borken = true;
		for(int i = -1; i < 2; ++i){
			for(int j = -1; j < 2; ++j){
				world.setBlockToAir(pos.offset(side.getAxis() == Axis.X ? EnumFacing.UP : EnumFacing.EAST, i).offset(side.getAxis() == Axis.Z ? EnumFacing.UP : EnumFacing.NORTH, j));
			}
		}
		if(drop){
			world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(GearFactory.gearTypes.get(type).getLargeGear(), 1)));
		}
	}

	@Override
	public void update(){
		if(world.isRemote){
			angleW[0] += angleW[1] * 9D / Math.PI;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
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
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
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
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		if(type != null){
			nbt.setInteger("type", type.getIndex());
		}
		nbt.setBoolean("new", true);
		nbt.setFloat("angle", angleW[0]);
		nbt.setFloat("cl_w", angleW[1]);
		return nbt;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable EntityPlayerMP sendingPlayer){
		if(identifier == 0){
			float angle = Float.intBitsToFloat((int) (message & 0xFFFFFFFFL));
			angleW[0] = Math.abs(angle - angleW[0]) > 5F ? angle : angleW[0];
			angleW[1] = Float.intBitsToFloat((int) (message >>> 32L));
		}else if(identifier == 1){
			type = message < 0 || message >= GearFactory.gearMats.size() ? GearFactory.gearMats.get(0) : GearFactory.gearMats.get((int) message);
		}
	}

	private final AxleHandler handlerMain = new AxleHandler();

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.AXLE_HANDLER_CAPABILITY && facing == getFacing()){
			return type != null;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.AXLE_HANDLER_CAPABILITY && facing == getFacing()){
			return (T) handlerMain;
		}
		return super.getCapability(capability, facing);
	}

	private class AxleHandler implements IAxleHandler{

		private byte updateKey;
		private double rotRatio;

		@Override
		public double[] getMotionData(){
			return motionData;
		}

		@Override
		public void propogate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
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

			rotRatio = rotRatioIn;

			if(updateKey == 0){
				resetAngle();
			}
			updateKey = key;
			
			for(EnumFacing.AxisDirection dir : EnumFacing.AxisDirection.values()){
				EnumFacing axleDir = dir == EnumFacing.AxisDirection.POSITIVE ? getFacing() : getFacing().getOpposite();
				TileEntity connectTE = world.getTileEntity(pos.offset(axleDir));
				
				if(connectTE != null){
					if(connectTE.hasCapability(Capabilities.AXIS_HANDLER_CAPABILITY, axleDir.getOpposite())){
						connectTE.getCapability(Capabilities.AXIS_HANDLER_CAPABILITY, axleDir.getOpposite()).trigger(masterIn, key);
					}
					if(connectTE.hasCapability(Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY, axleDir.getOpposite())){
						masterIn.addAxisToList(connectTE.getCapability(Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY, axleDir.getOpposite()), axleDir.getOpposite());
					}

					if(connectTE.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, axleDir.getOpposite())){
						connectTE.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, axleDir.getOpposite()).propogate(masterIn, key, rotRatio, 0);
					}
				}
			}


			EnumFacing side = getFacing();
			
			for(int i = 0; i < 6; i++){
				if(i != side.getIndex() && i != side.getOpposite().getIndex()){
					EnumFacing facing = EnumFacing.byIndex(i);
					// Adjacent gears
					TileEntity adjTE = world.getTileEntity(pos.offset(facing, 2));
					if(adjTE != null){
						if(adjTE.hasCapability(Capabilities.COG_HANDLER_CAPABILITY, side)){
							adjTE.getCapability(Capabilities.COG_HANDLER_CAPABILITY, side).connect(masterIn, key, -rotRatio, 1.5D, facing.getOpposite());
						}else if(adjTE.hasCapability(Capabilities.COG_HANDLER_CAPABILITY, facing.getOpposite())){
							//Check for large gears
							adjTE.getCapability(Capabilities.COG_HANDLER_CAPABILITY, facing.getOpposite()).connect(masterIn, key, RotaryUtil.getDirSign(side, facing) * rotRatio, 1.5D, side);
						}
					}

					// Diagonal gears
					TileEntity diagTE = world.getTileEntity(pos.offset(facing, 2).offset(side));
					if(diagTE != null && diagTE.hasCapability(Capabilities.COG_HANDLER_CAPABILITY, facing.getOpposite()) && RotaryUtil.canConnectThrough(world, pos.offset(facing, 2), facing.getOpposite(), side)){
						diagTE.getCapability(Capabilities.COG_HANDLER_CAPABILITY, facing.getOpposite()).connect(masterIn, key, -RotaryUtil.getDirSign(side, facing) * rotRatio, 1.5D, side.getOpposite());
					}

					//Underside gears
					TileEntity undersideTE = world.getTileEntity(pos.offset(facing, 1).offset(side));
					if(undersideTE != null && undersideTE.hasCapability(Capabilities.COG_HANDLER_CAPABILITY, facing)){
						undersideTE.getCapability(Capabilities.COG_HANDLER_CAPABILITY, facing).connect(masterIn, key, RotaryUtil.getDirSign(side, facing) * rotRatioIn, 1.5D, side.getOpposite());
					}
				}
			}
		}

		@Override
		public double getMoInertia(){
			return inertia;
		}

		@Override
		public void resetAngle(){
			if(!world.isRemote){
				angleW[1] = 0;
				angleW[0] = Math.signum(rotRatio * getFacing().getAxisDirection().getOffset()) == -1 ? 7.5F : 0F;
				ModPackets.network.sendToAllAround(new SendLongToClient((byte) 0, (Float.floatToIntBits(angleW[0]) & 0xFFFFFFFFL) | ((long) Float.floatToIntBits(angleW[1]) << 32L), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
		}

		@Override
		public float getAngle(){
			return angleW[0];
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

		@Override
		public void setAngle(float angleIn){
			angleW[0] = angleIn;
		}

		@Override
		public float getClientW(){
			return angleW[1];
		}

		@Override
		public void syncAngle(){
			angleW[1] = (float) motionData[0];
			ModPackets.network.sendToAllAround(new SendLongToClient((byte) 0, (Float.floatToIntBits(angleW[0]) & 0xFFFFFFFFL) | ((long) Float.floatToIntBits(angleW[1]) << 32L), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
	}
}

package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.packets.ISpinReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendSpinToClient;
import com.Da_Technomancer.crossroads.API.rotary.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class ToggleGearTileEntity extends TileEntity implements ITickable, ISpinReceiver{

	private GearTypes type;
	private double[] motionData = new double[4];
	private double inertia = 0;
	private float angle;
	private float clientW;
	private double compOut = 0;

	public ToggleGearTileEntity(){
		super();
	}

	public ToggleGearTileEntity(GearTypes type){
		super();
		this.type = type;
		inertia = type.getDensity() / 64D;
	}

	public GearTypes getMember(){
		return type;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void update(){
		if(world.isRemote){
			// it's 9 / PI instead of 180 / PI because 20 ticks/second
			angle += clientW * 9D / Math.PI;
		}

		if(!world.isRemote){
			if(compOut != Math.abs(motionData[1] / inertia) * 15D){
				world.updateComparatorOutputLevel(pos, blockType);
				compOut = Math.abs(motionData[1] / inertia) * 15D;
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		// motionData
		NBTTagCompound motionTags = new NBTTagCompound();
		for(int i = 0; i < 3; i++){
			if(motionData[i] != 0)
				motionTags.setDouble(i + "motion", motionData[i]);
		}
		nbt.setTag("motionData", motionTags);
		nbt.setFloat("clientW", clientW);
		nbt.setFloat("angle", angle);
		// member
		if(type != null){
			nbt.setString("type", type.name());
		}
		nbt.setDouble("comp", compOut);

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		// motionData
		NBTTagCompound innerMot = nbt.getCompoundTag("motionData");
		for(int i = 0; i < 4; i++){
			motionData[i] = innerMot.getDouble(i + "motion");
		}

		//type
		type = nbt.hasKey("type") ? GearTypes.valueOf(nbt.getString("type")) : null;
		if(type != null){
			inertia = type.getDensity() / 64D;
		}
		compOut = nbt.getDouble("comp");
		clientW = nbt.getFloat("clientW");
		angle = nbt.getFloat("angle");
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		if(type != null){
			nbt.setString("type", type.name());
		}
		nbt.setFloat("clientW", clientW);
		nbt.setFloat("angle", angle);
		return nbt;
	}

	@Override
	public void receiveSpin(int identifier, float clientW, float angle){
		if(identifier == 0){
			this.clientW = clientW;
			this.angle = Math.abs(angle - this.angle) > 15F ? angle : this.angle;
		}
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return Block.FULL_BLOCK_AABB.offset(pos);
	}

	private final IAxleHandler axleHandler = new AxleHandler();
	private final ICogHandler cogHandler = new CogHandler();
	private final RedstoneHandler redstoneHandler = new RedstoneHandler();

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.AXLE_HANDLER_CAPABILITY && facing == EnumFacing.DOWN){
			return true;
		}
		if(capability == Capabilities.COG_HANDLER_CAPABILITY && facing == EnumFacing.DOWN && world.getBlockState(pos).getValue(Properties.REDSTONE_BOOL)){
			return true;
		}
		if(capability == Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY){
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.AXLE_HANDLER_CAPABILITY && facing == EnumFacing.DOWN){
			return (T) axleHandler;
		}
		if(capability == Capabilities.COG_HANDLER_CAPABILITY && facing == EnumFacing.DOWN && world.getBlockState(pos).getValue(Properties.REDSTONE_BOOL)){
			return (T) cogHandler;
		}
		if(capability == Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY){
			return (T) redstoneHandler;
		}

		return super.getCapability(capability, facing);
	}

	private class RedstoneHandler implements IAdvancedRedstoneHandler{

		@Override
		public double getOutput(boolean measure){
			return measure ? Math.abs(motionData[0]) / 2D : 0;
		}
	}

	private class CogHandler implements ICogHandler{

		@Override
		public void connect(IAxisHandler masterIn, byte key, double rotationRatioIn, double lastRadius){
			axleHandler.propogate(masterIn, key, rotationRatioIn, lastRadius);
		}

		@Override
		public IAxleHandler getAxle(){
			return axleHandler;
		}
	}

	private class AxleHandler implements IAxleHandler{

		private byte key;
		private double rotRatio;

		@Override
		public double[] getMotionData(){
			return motionData;
		}

		@Override
		public void propogate(IAxisHandler masterIn, byte keyIn, double rotRatioIn, double lastRadius){
			if(lastRadius != 0){
				rotRatioIn *= -lastRadius / .5D;
			}else if(rotRatioIn == 0){
				rotRatioIn = 1;
			}

			//If true, this has already been checked.
			if(key == keyIn){
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

			if(key == 0){
				resetAngle();
			}
			key = keyIn;

			TileEntity downTE = world.getTileEntity(pos.offset(EnumFacing.DOWN));
			if(downTE != null){
				if(downTE.hasCapability(Capabilities.AXIS_HANDLER_CAPABILITY, EnumFacing.UP)){
					downTE.getCapability(Capabilities.AXIS_HANDLER_CAPABILITY, EnumFacing.UP).trigger(masterIn, key);
				}
				if(downTE.hasCapability(Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY, EnumFacing.UP)){
					masterIn.addAxisToList(downTE.getCapability(Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY, EnumFacing.UP), EnumFacing.UP);
				}
				if(downTE.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP)){
					downTE.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP).propogate(masterIn, key, rotRatio, 0);
				}
			}


			if(world.getBlockState(pos).getValue(Properties.REDSTONE_BOOL)){
				for(int i = 2; i < 6; ++i){
					EnumFacing facing = EnumFacing.getFront(i);
					// Adjacent gears
					TileEntity adjTE = world.getTileEntity(pos.offset(facing));
					if(adjTE != null && adjTE.hasCapability(Capabilities.COG_HANDLER_CAPABILITY, EnumFacing.DOWN)){
						adjTE.getCapability(Capabilities.COG_HANDLER_CAPABILITY, EnumFacing.DOWN).connect(masterIn, key, rotRatio, .5D);
					}

					// Diagonal gears
					TileEntity diagTE = world.getTileEntity(pos.offset(facing).offset(EnumFacing.DOWN));
					if(diagTE != null && diagTE.hasCapability(Capabilities.COG_HANDLER_CAPABILITY, facing.getOpposite()) && DefaultAxleHandler.canConnectThrough(world, pos.offset(facing), facing.getOpposite(), EnumFacing.DOWN)){
						diagTE.getCapability(Capabilities.COG_HANDLER_CAPABILITY, facing.getOpposite()).connect(masterIn, key, rotRatio, .5D);
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
				clientW = 0;
				angle = Math.signum(rotRatio) == -1 ? 22.5F : 0F;
				SendSpinToClient msg = new SendSpinToClient(0, clientW, angle, pos);
				ModPackets.network.sendToAllAround(msg, new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
		}

		@Override
		public float getAngle(){
			return angle;
		}

		@Override
		public void addEnergy(double energy, boolean allowInvert, boolean absolute){
			if(allowInvert && absolute){
				motionData[1] += energy;
			}else if(allowInvert){
				motionData[1] += energy * Math.signum(motionData[1]);
			}else if(absolute){
				int sign = (int) Math.signum(motionData[1]);
				motionData[1] += energy;
				if(sign != 0 && Math.signum(motionData[1]) != sign){
					motionData[1] = 0;
				}
			}else{
				int sign = (int) Math.signum(motionData[1]);
				motionData[1] += energy * ((double) sign);
				if(Math.signum(motionData[1]) != sign){
					motionData[1] = 0;
				}
			}
			markDirty();
		}

		@Override
		public double getRotationRatio(){
			return rotRatio;
		}

		@Override
		public void markChanged(){
			markDirty();
		}

		@SideOnly(Side.CLIENT)
		@Override
		public float getNextAngle(){
			return angle + (clientW * 9F / (float) Math.PI);
		}

		@Override
		public boolean shouldManageAngle(){
			return true;
		}

		@Override
		public void setAngle(float angleIn){
			angle = angleIn;
		}

		@Override
		public float getClientW(){
			return clientW;
		}

		@Override
		public void syncAngle(){
			clientW = (float) motionData[0];
			SendSpinToClient msg = new SendSpinToClient(0, clientW, angle, pos);
			ModPackets.network.sendToAllAround(msg, new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
	}
}

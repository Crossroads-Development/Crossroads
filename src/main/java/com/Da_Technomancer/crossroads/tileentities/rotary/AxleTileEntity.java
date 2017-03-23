package com.Da_Technomancer.crossroads.tileentities.rotary;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.packets.IDoubleReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendDoubleToClient;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class AxleTileEntity extends TileEntity implements ITickable, IDoubleReceiver{

	private final int tiers = ModConfig.speedTiers.getInt();
	private double[] motionData = new double[4];
	private double[] physData = {125, .25D};
	private double angle;
	private double clientW;
	
	@Override
	public void update(){
		if(world.isRemote){
			if(clientW == Double.POSITIVE_INFINITY){
				angle = 0;
			}else if(clientW == Double.NEGATIVE_INFINITY){
				angle = 22.5;
			}else{
				// it's 9 / PI instead of 180 / PI because 20 ticks/second
				angle += clientW * 9D / Math.PI;
			}
		}

		if(!world.isRemote){
			sendWPacket();
		}
	}

	private void sendWPacket(){
		boolean flag = false;
		if(clientW == Double.POSITIVE_INFINITY || clientW == Double.NEGATIVE_INFINITY){
			flag = true;
		}else if(MiscOp.tiersRound(motionData[0], tiers) != clientW){
			flag = true;
			clientW = MiscOp.tiersRound(motionData[0], tiers);
		}

		if(flag){
			SendDoubleToClient msg = new SendDoubleToClient("w", clientW, pos);
			ModPackets.network.sendToAllAround(msg, new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));

			if(clientW == Double.POSITIVE_INFINITY || clientW == Double.NEGATIVE_INFINITY){
				clientW = 0;
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound){
		super.writeToNBT(compound);

		// motionData
		NBTTagCompound motionTags = new NBTTagCompound();
		for(int i = 0; i < 3; i++){
			if(motionData[i] != 0)
				motionTags.setDouble(i + "motion", motionData[i]);
		}
		compound.setTag("motionData", motionTags);

		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound){
		super.readFromNBT(compound);

		// motionData
		NBTTagCompound innerMot = compound.getCompoundTag("motionData");
		for(int i = 0; i < 4; i++){
			this.motionData[i] = (innerMot.hasKey(i + "motion")) ? innerMot.getDouble(i + "motion") : 0;
		}
	}

	@Override
	public void receiveDouble(String context, double message){
		if(context.equals("w")){
			clientW = message;
		}
	}

	private final IAxleHandler axleHandler = new AxleHandler();
	
	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.AXLE_HANDLER_CAPABILITY && facing != null && facing.getAxis() == world.getBlockState(pos).getValue(Properties.AXIS)){
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.AXLE_HANDLER_CAPABILITY && facing != null && facing.getAxis() == world.getBlockState(pos).getValue(Properties.AXIS)){
			return (T) axleHandler;
		}
		return super.getCapability(capability, facing);
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
			if(rotRatioIn == 0){
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

			EnumFacing endPos = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE, world.getBlockState(pos).getValue(Properties.AXIS));
			EnumFacing endNeg = endPos.getOpposite();
			
			if(world.getTileEntity(pos.offset(endPos)) != null && world.getTileEntity(pos.offset(endPos)).hasCapability(Capabilities.AXIS_HANDLER_CAPABILITY, endNeg)){
				world.getTileEntity(pos.offset(endPos)).getCapability(Capabilities.AXIS_HANDLER_CAPABILITY, endNeg).trigger(masterIn, key);
			}
			if(world.getTileEntity(pos.offset(endNeg)) != null && world.getTileEntity(pos.offset(endNeg)).hasCapability(Capabilities.AXIS_HANDLER_CAPABILITY, endPos)){
				world.getTileEntity(pos.offset(endNeg)).getCapability(Capabilities.AXIS_HANDLER_CAPABILITY, endPos).trigger(masterIn, key);
			}
			if(world.getTileEntity(pos.offset(endPos)) != null && world.getTileEntity(pos.offset(endPos)).hasCapability(Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY, endNeg)){
				masterIn.addAxisToList(world.getTileEntity(pos.offset(endPos)).getCapability(Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY, endNeg), endNeg);
			}
			if(world.getTileEntity(pos.offset(endNeg)) != null && world.getTileEntity(pos.offset(endNeg)).hasCapability(Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY, endPos)){
				masterIn.addAxisToList(world.getTileEntity(pos.offset(endNeg)).getCapability(Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY, endPos), endPos);
			}

			if(world.getTileEntity(pos.offset(endNeg)) != null && world.getTileEntity(pos.offset(endNeg)).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, endPos)){
				world.getTileEntity(pos.offset(endNeg)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, endPos).propogate(masterIn, key, rotRatio, 0);
			}
			if(world.getTileEntity(pos.offset(endPos)) != null && world.getTileEntity(pos.offset(endPos)).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, endNeg)){
				world.getTileEntity(pos.offset(endPos)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, endNeg).propogate(masterIn, key, rotRatio, 0);
			}
		}

		@Override
		public double[] getPhysData(){
			return physData;
		}

		@Override
		public void resetAngle(){
			if(!world.isRemote){
				clientW = (MiscOp.posOrNeg(rotRatio) == -1 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
			}
		}

		@Override
		public double getAngle(){
			return angle;
		}

		@Override
		public void addEnergy(double energy, boolean allowInvert, boolean absolute){
			if(allowInvert && absolute){
				motionData[1] += energy;
			}else if(allowInvert){
				motionData[1] += energy * MiscOp.posOrNeg(motionData[1]);
			}else if(absolute){
				int sign = (int) MiscOp.posOrNeg(motionData[1]);
				motionData[1] += energy;
				if(sign != 0 && MiscOp.posOrNeg(motionData[1]) != sign){
					motionData[1] = 0;
				}
			}else{
				int sign = (int) MiscOp.posOrNeg(motionData[1]);
				motionData[1] += energy * ((double) sign);
				if(MiscOp.posOrNeg(motionData[1]) != sign){
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
	}
}

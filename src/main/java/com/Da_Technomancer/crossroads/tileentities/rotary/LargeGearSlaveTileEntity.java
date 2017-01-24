package com.Da_Technomancer.crossroads.tileentities.rotary;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.packets.IPosReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendPosToClient;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.ICogHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class LargeGearSlaveTileEntity extends TileEntity implements IPosReceiver{

	private BlockPos masterPos;

	public void setInitial(BlockPos masPos){
		masterPos = masPos;
		SendPosToClient msg = new SendPosToClient("init", masterPos, pos);
		ModPackets.network.sendToAllAround(msg, new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
	}

	@Override
	public void receivePos(String context, BlockPos message){
		if(context.equals("init")){
			masterPos = message;
		}
	}

	public void passBreak(EnumFacing side){
		if(worldObj.getTileEntity(masterPos) instanceof LargeGearMasterTileEntity){
			((LargeGearMasterTileEntity) worldObj.getTileEntity(masterPos)).breakGroup(side);
		}
	}

	private boolean isEdge(){
		if(masterPos != null && masterPos.distanceSq(pos) == 1){
			return true;
		}
		return false;
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setLong("mast", masterPos.toLong());
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		this.masterPos = BlockPos.fromLong(nbt.getLong("mast"));
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setLong("mast", masterPos.toLong());
		return nbt;
	}

	private final ICogHandler handler = new CogHandler();
	
	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.COG_HANDLER_CAPABILITY && isEdge() && worldObj.getBlockState(pos).getValue(Properties.FACING) == facing){
			return true;
		}else{
			return super.hasCapability(capability, facing);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.COG_HANDLER_CAPABILITY && isEdge() && worldObj.getBlockState(pos).getValue(Properties.FACING) == facing){
			return (T) handler;
		}else{
			return super.getCapability(capability, facing);
		}
	}
	
	private class CogHandler implements ICogHandler{

		@Override
		public void connect(IAxisHandler masterIn, byte key, double rotationRatioIn, double lastRadius){
			getAxle().propogate(masterIn, key, rotationRatioIn, lastRadius);
		}

		@Override
		public IAxleHandler getAxle(){
			return worldObj.getTileEntity(masterPos) != null ? worldObj.getTileEntity(masterPos).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, worldObj.getBlockState(pos).getValue(Properties.FACING)) : null;
		}
	}
}

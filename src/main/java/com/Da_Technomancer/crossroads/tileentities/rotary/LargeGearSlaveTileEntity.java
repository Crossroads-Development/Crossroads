package com.Da_Technomancer.crossroads.tileentities.rotary;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.packets.IPosReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendPosToClient;
import com.Da_Technomancer.crossroads.API.rotary.ISlaveGear;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class LargeGearSlaveTileEntity extends TileEntity implements IPosReceiver, ISlaveGear{

	private BlockPos masterPos;

	public void setInitial(BlockPos masPos){

		masterPos = masPos;
		SendPosToClient msg = new SendPosToClient("init", masterPos, pos);
		ModPackets.network.sendToAllAround(msg, new TargetPoint(worldObj.provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 512));
	}

	@Override
	public void receivePos(String context, BlockPos message){
		if(context.equals("init")){
			masterPos = message;
		}
	}

	public void passBreak(){
		if(worldObj.getTileEntity(masterPos) instanceof LargeGearMasterTileEntity){
			((LargeGearMasterTileEntity) worldObj.getTileEntity(masterPos)).breakGroup();
		}
	}

	private boolean isEdge(){
		if(masterPos.distanceSq(pos) == 1){
			return true;
		}
		return false;
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

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(isEdge() && worldObj.getTileEntity(masterPos) instanceof LargeGearMasterTileEntity){
			return worldObj.getTileEntity(masterPos).hasCapability(capability, facing);
		}else{
			return super.hasCapability(capability, facing);
		}
	}

	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(isEdge() && worldObj.getTileEntity(masterPos) instanceof LargeGearMasterTileEntity){
			return worldObj.getTileEntity(masterPos).getCapability(capability, facing);
		}else{
			return super.getCapability(capability, facing);
		}
	}
}

package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.fields.FieldWorldSavedData;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class RateManipulatorTileEntity extends TileEntity implements ITickable{

	private boolean run = false;

	@Override
	public void update(){
		if(!worldObj.isRemote){
			if(worldObj.getTotalWorldTime() % 5 == 0 && !run){
				if(worldObj.getTileEntity(pos.offset(EnumFacing.UP)) != null && worldObj.getTileEntity(pos.offset(EnumFacing.UP)).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN)){
					FieldWorldSavedData data = FieldWorldSavedData.get(worldObj);
					if(data.fieldNodes.containsKey(FieldWorldSavedData.getLongFromChunk(worldObj.getChunkFromBlockCoords(pos)))){
						data.nodeForces.get(FieldWorldSavedData.getLongFromChunk(worldObj.getChunkFromBlockCoords(pos)))[1][FieldWorldSavedData.getChunkRelativeCoord(pos.getX()) / 2][FieldWorldSavedData.getChunkRelativeCoord(pos.getZ()) / 2] += worldObj.getTileEntity(pos.offset(EnumFacing.UP)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN).getMotionData()[0] * 16;
					}
				}
				run = true;
			}else if(worldObj.getTotalWorldTime() % 5 == 1){
				run = false;
			}
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("run", run);
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		run = nbt.getBoolean("run");
	}
}

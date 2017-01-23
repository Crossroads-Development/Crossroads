package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.fields.FieldWorldSavedData;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class FluxManipulatorTileEntity extends TileEntity implements ITickable{

	private int lastTick = -1;
	private double netForce;
	//Value derived from 1 revolution = 32 flux.
	private static final double FLUX_FROM_SPEED = .8D / Math.PI;

	@Override
	public void update(){
		if(!worldObj.isRemote){
			if(worldObj.getTotalWorldTime() % 5 != lastTick){
				if(worldObj.getTileEntity(pos.offset(EnumFacing.UP)) != null && worldObj.getTileEntity(pos.offset(EnumFacing.UP)).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN)){
					FieldWorldSavedData data = FieldWorldSavedData.get(worldObj);
					if(data.fieldNodes.containsKey(FieldWorldSavedData.getLongFromChunk(worldObj.getChunkFromBlockCoords(pos)))){
						netForce += worldObj.getTileEntity(pos.offset(EnumFacing.UP)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN).getMotionData()[0] * FLUX_FROM_SPEED;
						if(worldObj.getTotalWorldTime() % 5 == 0){
							data.nodeForces.get(FieldWorldSavedData.getLongFromChunk(worldObj.getChunkFromBlockCoords(pos)))[0][FieldWorldSavedData.getChunkRelativeCoord(pos.getX()) / 2][FieldWorldSavedData.getChunkRelativeCoord(pos.getZ()) / 2] += netForce;
							netForce = 0;
						}
					}
				}
				lastTick = (int) (worldObj.getTotalWorldTime() % 5);
			}
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("lastTick", lastTick);
		nbt.setDouble("netForce", netForce);
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		lastTick = nbt.getInteger("lastTick");
		netForce = nbt.getDouble("netForce");
	}
}

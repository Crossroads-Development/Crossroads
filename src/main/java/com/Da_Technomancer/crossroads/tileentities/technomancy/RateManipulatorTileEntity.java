package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.technomancy.FieldWorldSavedData;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class RateManipulatorTileEntity extends TileEntity implements ITickable{

	private boolean run = false;

	@Override
	public void update(){
		if(!world.isRemote){
			if(world.getTotalWorldTime() % 5 == 0 && !run){
				if(world.getTileEntity(pos.offset(EnumFacing.UP)) != null && world.getTileEntity(pos.offset(EnumFacing.UP)).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN)){
					FieldWorldSavedData data = FieldWorldSavedData.get(world);
					if(data.fieldNodes.containsKey(FieldWorldSavedData.getLongFromPos(pos))){
						data.nodeForces.get(FieldWorldSavedData.getLongFromPos(pos))[1][FieldWorldSavedData.getChunkRelativeCoord(pos.getX()) / 2][FieldWorldSavedData.getChunkRelativeCoord(pos.getZ()) / 2] += world.getTileEntity(pos.offset(EnumFacing.UP)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN).getMotionData()[0] / EnergyConverters.SPEED_PER_RATE;
					}
				}
				run = true;
			}else if(world.getTotalWorldTime() % 5 == 1){
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

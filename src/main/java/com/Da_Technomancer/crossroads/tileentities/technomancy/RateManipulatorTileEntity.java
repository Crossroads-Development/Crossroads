package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.technomancy.FieldWorldSavedData;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.ChunkPos;

public class RateManipulatorTileEntity extends TileEntity implements ITickable{

	private boolean run = false;

	@Override
	public void update(){
		if(!world.isRemote){
			if(world.getTotalWorldTime() % 5 == 0 && !run){
				TileEntity upTE = world.getTileEntity(pos.offset(EnumFacing.UP));
				if(upTE != null && upTE.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN)){
					FieldWorldSavedData data = FieldWorldSavedData.get(world);
					if(data.fieldNodes.containsKey(MiscOp.getLongFromChunkPos(new ChunkPos(pos)))){
						data.nodeForces.get(MiscOp.getLongFromChunkPos(new ChunkPos(pos)))[1][MiscOp.getChunkRelativeCoord(pos.getX()) / 2][MiscOp.getChunkRelativeCoord(pos.getZ()) / 2] += upTE.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN).getMotionData()[0] * EnergyConverters.RATE_PER_SPEED;
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

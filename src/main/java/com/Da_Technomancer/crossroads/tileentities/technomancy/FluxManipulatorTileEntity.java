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

public class FluxManipulatorTileEntity extends TileEntity implements ITickable{

	private int lastTick = -1;
	private double netForce;

	@Override
	public void update(){
		if(!world.isRemote && world.getTotalWorldTime() % 5 != lastTick){
			TileEntity upTE = world.getTileEntity(pos.offset(EnumFacing.UP));
			if(upTE != null && upTE.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN)){
				FieldWorldSavedData data = FieldWorldSavedData.get(world);
				if(data.fieldNodes.containsKey(MiscOp.getLongFromChunkPos(new ChunkPos(pos)))){
					//Gear Speed * Time Applied (1 tick = 1/20 seconds) / SPEED_PER_FLUX (1/SPEED_PER_FLUX would = FLUX_PER_RADIAN if that was defined).
					netForce += upTE.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN).getMotionData()[0] / (20D * EnergyConverters.SPEED_PER_FLUX);
					if(world.getTotalWorldTime() % 5 == 0){
						data.nodeForces.get(MiscOp.getLongFromChunkPos(new ChunkPos(pos)))[0][MiscOp.getChunkRelativeCoord(pos.getX()) / 2][MiscOp.getChunkRelativeCoord(pos.getZ()) / 2] += Math.round(netForce);
						netForce = 0;
					}
				}else{
					netForce = 0;
				}
			}
			lastTick = (int) (world.getTotalWorldTime() % 5);
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

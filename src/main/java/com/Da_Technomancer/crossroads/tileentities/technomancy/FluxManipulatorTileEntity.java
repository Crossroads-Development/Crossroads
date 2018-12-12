package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscUtil;
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
			if(upTE != null && upTE.hasCapability(Capabilities.AXLE_CAPABILITY, EnumFacing.DOWN)){
				FieldWorldSavedData data = FieldWorldSavedData.get(world);
				if(data.fieldNodes.containsKey(MiscUtil.getLongFromChunkPos(new ChunkPos(pos)))){
					netForce += upTE.getCapability(Capabilities.AXLE_CAPABILITY, EnumFacing.DOWN).getMotionData()[0] / 5D;
					if(world.getTotalWorldTime() % 5 == 0){
						data.fieldNodes.get(MiscUtil.getLongFromChunkPos(new ChunkPos(pos))).fluxForce += Math.round(netForce);
						netForce = 0;
					}
				}else{
					netForce = 0;
				}
			}
			lastTick = (int) (world.getTotalWorldTime() % 5);
			markDirty();
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

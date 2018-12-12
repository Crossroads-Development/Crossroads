package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.technomancy.ChunkField;
import com.Da_Technomancer.crossroads.API.technomancy.FieldWorldSavedData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.ChunkPos;

public class RateManipulatorTileEntity extends TileEntity implements ITickable{

	private boolean run = false;
	public int range = 0;

	@Override
	public void update(){
		if(!world.isRemote){
			if(world.getTotalWorldTime() % 5 == 0 && !run){
				TileEntity upTE = world.getTileEntity(pos.offset(EnumFacing.UP));
				if(upTE != null && upTE.hasCapability(Capabilities.AXLE_CAPABILITY, EnumFacing.DOWN)){
					FieldWorldSavedData data = FieldWorldSavedData.get(world);
					if(data.fieldNodes.containsKey(MiscUtil.getLongFromChunkPos(new ChunkPos(pos)))){
						ChunkField nodes = data.fieldNodes.get(MiscUtil.getLongFromChunkPos(new ChunkPos(pos)));
						int force = (int) Math.round(upTE.getCapability(Capabilities.AXLE_CAPABILITY, EnumFacing.DOWN).getMotionData()[0]);
						for(int i = Math.max(0, MiscUtil.getChunkRelativeCoord(pos.getX()) - range); i <= Math.min(15, MiscUtil.getChunkRelativeCoord(pos.getX()) + range); i++){
							for(int j = Math.max(0, MiscUtil.getChunkRelativeCoord(pos.getZ()) - range); j <= Math.min(15, MiscUtil.getChunkRelativeCoord(pos.getZ()) + range); j++){
								nodes.nodeForce[i][j] += force;
							}
						}
					}
				}
				run = true;
				markDirty();
			}else if(world.getTotalWorldTime() % 5 == 1){
				run = false;
				markDirty();
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("run", run);
		nbt.setInteger("range", range);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		run = nbt.getBoolean("run");
		range = nbt.getInteger("range");
	}
}

package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.technomancy.ChunkField;
import com.Da_Technomancer.crossroads.API.technomancy.FieldWorldSavedData;
import com.Da_Technomancer.crossroads.blocks.Ratiator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.ChunkPos;

public class ChunkUnlockerTileEntity extends TileEntity implements ITickable{

	@Override
	public void update(){
		if(!world.isRemote && world.getTotalWorldTime() % 2 == 0 && Ratiator.getPowerOnSide(world, pos, EnumFacing.UP, true) != 0){
			FieldWorldSavedData data = FieldWorldSavedData.get(world);
			long key = MiscOp.getLongFromChunkPos(new ChunkPos(pos));
			if(data.fieldNodes.containsKey(key)){
				data.fieldNodes.get(key).refreshed = true;
			}else{
				data.fieldNodes.put(key, new ChunkField(false));
			}
		}
	}
}

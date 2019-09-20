package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.entity.EntityGhostMarker;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EqualibriumEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, int mult, Direction dir){
		EntityGhostMarker marker = new EntityGhostMarker(worldIn, EntityGhostMarker.EnumMarkerType.EQUALIBRIUM);
		marker.setPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		CompoundNBT rangeData = new CompoundNBT();
		rangeData.setInteger("range", (int) mult);
		marker.data = rangeData;
		worldIn.addEntity(marker);

		//Effect in crystal master axis
	}
	
	public static class VoidEqualibriumEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, int mult, Direction dir){
			EntityGhostMarker marker = new EntityGhostMarker(worldIn, EntityGhostMarker.EnumMarkerType.VOID_EQUALIBRIUM);
			marker.setPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
			CompoundNBT rangeData = new CompoundNBT();
			rangeData.setInteger("range", (int) mult);
			marker.data = rangeData;
			worldIn.addEntity(marker);

			//Effect in crystal master axis.
		}
	}
}

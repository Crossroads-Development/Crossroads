package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.entity.EntityGhostMarker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class EqualibriumEffect extends BeamEffect{

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, Level worldIn, BlockPos pos, @Nullable Direction dir){
		if(!performTransmute(align, voi, power, worldIn, pos)){
			if(voi){
				EntityGhostMarker marker = new EntityGhostMarker(worldIn, EntityGhostMarker.EnumMarkerType.VOID_EQUILIBRIUM);
				marker.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
				CompoundTag rangeData = new CompoundTag();
				rangeData.putInt("range", power);
				marker.data = rangeData;
				worldIn.addFreshEntity(marker);

				//Effect in crystal master axis.
			}else{
				EntityGhostMarker marker = new EntityGhostMarker(worldIn, EntityGhostMarker.EnumMarkerType.EQUILIBRIUM);
				marker.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
				CompoundTag rangeData = new CompoundTag();
				rangeData.putInt("range", power);
				marker.data = rangeData;
				worldIn.addFreshEntity(marker);

				//Effect in crystal master axis
			}
		}
	}
}

package com.Da_Technomancer.crossroads.effects.beam_effects;

import com.Da_Technomancer.crossroads.api.beams.BeamHit;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class LightEffect extends BeamEffect{

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, BeamHit beamHit){
		if(!performTransmute(align, voi, power, beamHit)){
			if(voi){
				//Break light sources nearby
				int range = (int) Math.sqrt(power) / 2;//0 to 4 radius
				BlockPos pos = beamHit.getPos();
				BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
				for(int i = -range; i <= range; i++){
					for(int j = -range; j <= range; j++){
						for(int k = -range; k <= range; k++){
							checkPos.set(pos.getX() + i, pos.getY() + j, pos.getZ() + k);
							BlockState state = beamHit.getWorld().getBlockState(checkPos);
							if(state.getLightEmission(beamHit.getWorld(), checkPos) > 0 && state.getDestroySpeed(beamHit.getWorld(), checkPos) < 0.5F){
								beamHit.getWorld().destroyBlock(checkPos, true);
							}
						}
					}
				}
			}else{
				//Spawn light clusters
				BlockState state = beamHit.getEndState();
				if(state.isAir()){
					beamHit.getWorld().setBlockAndUpdate(beamHit.getPos(), CRBlocks.lightCluster.defaultBlockState());
				}else if(state.getBlock() != CRBlocks.lightCluster && state.getLightEmission(beamHit.getWorld(), beamHit.getPos()) == 0 && state.isSolidRender(beamHit.getWorld(), beamHit.getPos())){//Don't spawn clusters against other light sources
					BlockPos offsetPos = beamHit.getPos().relative(beamHit.getDirection());
					state = beamHit.getWorld().getBlockState(offsetPos);
					if(state.isAir()){
						beamHit.getWorld().setBlockAndUpdate(offsetPos, CRBlocks.lightCluster.defaultBlockState());
					}
				}
			}
		}
	}
}

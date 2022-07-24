package com.Da_Technomancer.crossroads.effects.beam_effects;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.beams.BeamHit;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class EnergizeEffect extends BeamEffect{

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, BeamHit beamHit){
		if(!performTransmute(align, voi, power, beamHit)){
			IHeatHandler hitHandler = beamHit.getEndCapability(Capabilities.HEAT_CAPABILITY);

			if(voi){
				//Cold beam
				if(hitHandler != null){
					int mult = CRConfig.beamHeatMult.get();
					hitHandler.addHeat(-Math.min(mult * power, hitHandler.getTemp() - HeatUtil.ABSOLUTE_ZERO));
					//Effect in crystal master axis
				}else{
					//Get hit entities, extinguish
					List<Entity> entities = beamHit.getNearbyEntities(Entity.class, BeamHit.WITHIN_BLOCK_RANGE, null);
					for(Entity ent : entities){
						ent.clearFire();
					}

					BlockState state = beamHit.getEndState();
					if(state.getBlock() == Blocks.FIRE){
						//Extinguish fires
						beamHit.getWorld().setBlockAndUpdate(beamHit.getPos(), Blocks.AIR.defaultBlockState());
					}else{
						//Extinguish fire w/ offset
						BlockPos offsetPos = beamHit.getPos().relative(beamHit.getDirection());
						state = beamHit.getWorld().getBlockState(offsetPos);
						if(state.getBlock() == Blocks.FIRE){
							beamHit.getWorld().setBlockAndUpdate(offsetPos, Blocks.AIR.defaultBlockState());
						}
					}
				}
			}else{
				//Hot beam
				if(hitHandler != null){
					int mult = CRConfig.beamHeatMult.get();
					hitHandler.addHeat(Math.min(mult * power, HeatUtil.MAX_TEMP - hitHandler.getTemp()));
					//Effect in crystal master axis
				}else{
					//Get hit entities, set them on fire
					List<Entity> entities = beamHit.getNearbyEntities(Entity.class, BeamHit.WITHIN_BLOCK_RANGE, null);
					for(Entity ent : entities){
						ent.setSecondsOnFire(power);
					}

					BlockState state = beamHit.getEndState();
					if(state.isAir()){
						//Set fires
						beamHit.getWorld().setBlockAndUpdate(beamHit.getPos(), Blocks.FIRE.defaultBlockState());
					}else{
						//Set a fire w/ offset
						BlockPos offsetPos = beamHit.getPos().relative(beamHit.getDirection());
						state = beamHit.getWorld().getBlockState(offsetPos);
						if(state.isAir()){
							beamHit.getWorld().setBlockAndUpdate(offsetPos, Blocks.FIRE.defaultBlockState());
						}
					}
				}
			}
		}
	}
}

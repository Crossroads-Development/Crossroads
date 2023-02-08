package com.Da_Technomancer.crossroads.effects.beam_effects;

import com.Da_Technomancer.crossroads.api.beams.BeamHit;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.entity.EntityGhostMarker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;

public class ExplosionEffect extends BeamEffect{

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, BeamHit beamHit){
		if(!performTransmute(align, voi, power, beamHit)){
			if(voi){
				Vec3 hitPos = beamHit.getHitPos();
				beamHit.getWorld().explode(null, hitPos.x, hitPos.y, hitPos.z, (int) Math.min(Math.ceil(power / 4D), 16), Explosion.BlockInteraction.BREAK);
			}else{
				//Suppress explosions
				Vec3 hitPos = beamHit.getHitPos();
				EntityGhostMarker marker = new EntityGhostMarker(beamHit.getWorld(), EntityGhostMarker.EnumMarkerType.EQUILIBRIUM);
				marker.setPos(hitPos.x, hitPos.y, hitPos.z);
				CompoundTag rangeData = new CompoundTag();
				rangeData.putInt("range", power);
				marker.data = rangeData;
				beamHit.getWorld().addFreshEntity(marker);

				//Effect in crystal master axis
			}
		}
	}
}

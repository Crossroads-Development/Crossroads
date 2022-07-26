package com.Da_Technomancer.crossroads.effects.beam_effects;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.beams.BeamHit;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class VoidEffect extends BeamEffect{

	protected static final DamageSource VOID = new DamageSource("void").setMagic().bypassArmor();
	protected static final DamageSource VOID_ABSOLUTE = new DamageSource("void").setMagic().bypassArmor().bypassMagic();

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, BeamHit beamHit){
		if(!performTransmute(align, voi, power, beamHit)){
			BlockState prev = beamHit.getEndState();
			if(prev.isAir()){
				//Attack entities
				List<Entity> entities = beamHit.getNearbyEntities(Entity.class, BeamHit.WITHIN_BLOCK_RANGE, null);
				boolean absoluteDamage = CRConfig.beamDamageAbsolute.get();
				for(Entity ent : entities){
					ent.hurt(absoluteDamage ? VOID_ABSOLUTE : VOID, power);
				}
				return;
			}

			//Destroy blocks
			if(CRConfig.isProtected(beamHit.getWorld(), beamHit.getPos(), prev)){
				return;
			}
			beamHit.getWorld().setBlockAndUpdate(beamHit.getPos(), Blocks.AIR.defaultBlockState());
			SoundType soundtype = prev.getSoundType(beamHit.getWorld(), beamHit.getPos(), null);
			beamHit.getWorld().playSound(null, beamHit.getPos(), soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
		}
	}
}

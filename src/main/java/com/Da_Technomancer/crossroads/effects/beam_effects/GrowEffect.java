package com.Da_Technomancer.crossroads.effects.beam_effects;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.beams.BeamHit;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.blocks.BlockSalt;
import com.Da_Technomancer.crossroads.entity.CRMobDamage;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class GrowEffect extends BeamEffect{

	//Crop types can be blacklisted from growth through the beam using the grow_blacklist tag. Intended for things like magical crops
	private static final TagKey<Block> growBlacklist = CraftingUtil.getTagKey(ForgeRegistries.Keys.BLOCKS, new ResourceLocation(Crossroads.MODID, "grow_blacklist"));

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, BeamHit beamHit){
		if(!performTransmute(align, voi, power, beamHit)){
			if(voi){
				//Affect mobs
				aoeKillHeal(power, beamHit, true);

				//Kill plants
				if(!BlockSalt.salinate(beamHit.getWorld(), beamHit.getPos())){
					//Also target the plant on this block so we can hit the soil and affect the plant on it
					BlockSalt.salinate(beamHit.getWorld(), beamHit.getPos().above());
				}
			}else{
				//Affect mobs
				aoeKillHeal(power, beamHit, false);

				//Bonemeal blocks

				//Optional config option to nerf the bonemeal effect
				int growMultiplier = CRConfig.growMultiplier.get();
				if(growMultiplier > 1){
					power = power / growMultiplier + ((beamHit.getWorld().random.nextInt(growMultiplier) < power % growMultiplier) ? 1 : 0);
				}

				BlockState state = beamHit.getEndState();
				BlockPos pos = beamHit.getPos();
				//We check above the hit block if it isn't growable, as that allows growing plants by hitting the soil
				if(!(state.getBlock() instanceof BonemealableBlock)){
					pos = pos.above();
					state = beamHit.getWorld().getBlockState(pos);
				}

				for(int i = 0; i < power; i++){
					if(!(state.getBlock() instanceof BonemealableBlock growable)){
						return;
					}

					if(CraftingUtil.tagContains(growBlacklist, state.getBlock())){
						return;
					}
					if(growable.isValidBonemealTarget(beamHit.getWorld(), pos, state, false)){
						growable.performBonemeal(beamHit.getWorld(), beamHit.getWorld().random, pos, state);
					}
					//The state must be queried every loop because some plants could break themselves upon growing
					state = beamHit.getWorld().getBlockState(pos);
				}
			}
		}
	}

	private static void aoeKillHeal(int power, BeamHit beamHit, boolean kill){
		double range = Math.sqrt(power) / 2D;
		float impact = (float) Math.ceil(power / 2F);
		List<LivingEntity> ents = beamHit.getNearbyEntities(LivingEntity.class, range, null);
		for(LivingEntity ent : ents){
			if(!kill ^ ent.isInvertedHealAndHarm()){
				ent.heal(impact);
			}else{
				ent.hurt(CRMobDamage.damageSource(CRMobDamage.POTENTIALVOID, beamHit.getWorld()), impact);
			}
		}
	}
}

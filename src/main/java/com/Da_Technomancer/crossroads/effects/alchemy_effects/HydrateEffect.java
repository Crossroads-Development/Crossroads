package com.Da_Technomancer.crossroads.effects.alchemy_effects;

import com.Da_Technomancer.crossroads.api.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.api.alchemy.IAlchEffect;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class HydrateEffect implements IAlchEffect{


	@Override
	public void doEffect(Level world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap reags){
		if(phase == EnumMatterPhase.LIQUID){
			// Based on vanilla's splash water bottle
			AABB aabb = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1F, pos.getY() + 1F, pos.getZ() + 1F);

			for(LivingEntity livingentity : world.getEntitiesOfClass(LivingEntity.class, aabb, ThrownPotion.WATER_SENSITIVE_OR_ON_FIRE)){
				if(livingentity.isSensitiveToWater()){
					livingentity.hurt(world.damageSources().magic(), 1.0F);
				}

				if(livingentity.isOnFire() && livingentity.isAlive()){
					livingentity.extinguishFire();
				}
			}

			for(Axolotl axolotl : world.getEntitiesOfClass(Axolotl.class, aabb)){
				axolotl.rehydrate();
			}

			BlockState state = world.getBlockState(pos);
			if(state.is(BlockTags.FIRE)){
				world.destroyBlock(pos, false, null);
			}else if(AbstractCandleBlock.isLit(state)){
				AbstractCandleBlock.extinguish(null, state, world, pos);
			}else if(CampfireBlock.isLitCampfire(state)){
				world.levelEvent(null, 1009, pos, 0);
				CampfireBlock.dowse(null, world, pos, state);
				world.setBlockAndUpdate(pos, state.setValue(CampfireBlock.LIT, Boolean.FALSE));
			}
		}
	}

	@Override
	public Component getName(){
		return Component.translatable("effect.hydrate");
	}
}

package com.Da_Technomancer.crossroads.effects.alchemy_effects;

import com.Da_Technomancer.crossroads.api.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.api.alchemy.IAlchEffect;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class ChlorineAlchemyEffect implements IAlchEffect{

	@Override
	public void doEffect(Level world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap reags){
		for(LivingEntity e : world.getEntitiesOfClass(LivingEntity.class, new AABB(pos, pos.offset(1, 1, 1)), EntitySelector.ENTITY_STILL_ALIVE)){
			e.addEffect(new MobEffectInstance(MobEffects.WITHER, 300, 3));
			e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 3));
			e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 1));
			e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 6000, 0));
			e.addEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 3));
			e.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 600, 0));//Sprinting is disabled while nauseous.
			e.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 600, 3));
			e.addEffect(new MobEffectInstance(MobEffects.POISON, 1200, 0));
		}
	}

	@Override
	public Component getName(){
		return Component.translatable("effect.poison");
	}
}

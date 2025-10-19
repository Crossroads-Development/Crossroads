package com.Da_Technomancer.crossroads.entity.mob_effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.EffectCures;

import javax.annotation.Nullable;

public class Curative extends MobEffect{

	public Curative(){
		super(MobEffectCategory.NEUTRAL, 0xFFFFFF);
		//Basically a milk bucket in potion form
	}

	@Override
	public void applyInstantenousEffect(@Nullable Entity p_180793_1_, @Nullable Entity p_180793_2_, LivingEntity self, int p_180793_4_, double p_180793_5_){
		if(!self.level().isClientSide){
			self.removeEffectsCuredBy(EffectCures.MILK);
		}
	}

	@Override
	public boolean isInstantenous(){
		return true;
	}
}

package com.Da_Technomancer.crossroads.entity.mob_effects;

import com.Da_Technomancer.crossroads.items.witchcraft.Syringe;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
			AttributeInstance maxHealthAttribute = self.getAttributes().getInstance(Attributes.MAX_HEALTH);
			if(maxHealthAttribute != null){
				//Curative also reverses the health penalty from permanent potion effects
				maxHealthAttribute.removeModifier(Syringe.HEALTH_PENALTY_ATTRIBUTE);
			}
		}
	}

	@Override
	public boolean isInstantenous(){
		return true;
	}
}

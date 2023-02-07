package com.Da_Technomancer.crossroads.entity.mob_effects;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class HealthPenalty extends MobEffect{

	public static final DamageSource NON_VIABLE = new DamageSource("non_viable").bypassArmor().bypassInvul();

	public HealthPenalty(){
		super(MobEffectCategory.HARMFUL, 0x006D00);

		addAttributeModifier(Attributes.MAX_HEALTH, "ABCDEF01-2345-4030-940E-514C1F160890", -1D, AttributeModifier.Operation.ADDITION);
	}

	@Override
	public void addAttributeModifiers(LivingEntity entity, AttributeMap manager, int intensity) {
		//The game won't let us reduce max health below 1
		//We check for the case where, were it not for the limit, that would happen
		//And deal the last point of damage
		boolean shouldKill = entity.getMaxHealth() - intensity <= 0;
		super.addAttributeModifiers(entity, manager, intensity);
		if(entity.getHealth() > entity.getMaxHealth()) {
			entity.setHealth(entity.getMaxHealth());
		}
		if(shouldKill){
			entity.hurt(NON_VIABLE, 1);
		}
	}
}

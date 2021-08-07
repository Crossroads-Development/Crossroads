package com.Da_Technomancer.crossroads.entity.mob_effects;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;

public class HealthPenalty extends Effect{

	public HealthPenalty(){
		super(EffectType.HARMFUL, 0x006D00);
		setRegistryName(Crossroads.MODID, "health_penalty");

		addAttributeModifier(Attributes.MAX_HEALTH, "ABCDEF01-2345-4030-940E-514C1F160890", -1D, AttributeModifier.Operation.ADDITION);
	}

	@Override
	public void addAttributeModifiers(LivingEntity entity, AttributeModifierManager manager, int intensity) {
		//The game won't let us reduce max health below 1
		//We check for the case where, were it not for the limit, that would happen
		//And deal the last point of damage
		boolean shouldKill = entity.getMaxHealth() - intensity <= 0;
		super.addAttributeModifiers(entity, manager, intensity);
		if(entity.getHealth() > entity.getMaxHealth()) {
			entity.setHealth(entity.getMaxHealth());
		}
		if(shouldKill){
			entity.hurt(DamageSource.OUT_OF_WORLD, 1);
		}
	}
}

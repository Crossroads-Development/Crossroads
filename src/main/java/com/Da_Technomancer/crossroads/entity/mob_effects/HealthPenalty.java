package com.Da_Technomancer.crossroads.entity.mob_effects;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class HealthPenalty extends Effect{

	public HealthPenalty(){
		super(EffectType.HARMFUL, 0x006D00);
		setRegistryName(Crossroads.MODID, "health_penalty");

		addAttributeModifier(Attributes.MAX_HEALTH, "ABCDEF01-2345-4030-940E-514C1F160890", -1D, AttributeModifier.Operation.ADDITION);
	}

	@Override
	public void addAttributeModifiers(LivingEntity entity, AttributeModifierManager manager, int p_111187_3_) {
		super.addAttributeModifiers(entity, manager, p_111187_3_);
		if(entity.getHealth() > entity.getMaxHealth()) {
			entity.setHealth(entity.getMaxHealth());
		}
	}
}

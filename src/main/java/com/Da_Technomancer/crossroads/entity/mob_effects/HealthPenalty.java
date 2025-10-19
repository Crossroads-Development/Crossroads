package com.Da_Technomancer.crossroads.entity.mob_effects;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.entity.CRMobDamage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class HealthPenalty extends MobEffect{

	private static final ResourceLocation HEALTH_MODIFIER_HEALTH_PENALTY_ID = ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "effect.health_penalty");

	public HealthPenalty(){
		super(MobEffectCategory.HARMFUL, 0x006D00);
		addAttributeModifier(Attributes.MAX_HEALTH, HEALTH_MODIFIER_HEALTH_PENALTY_ID, -1D, AttributeModifier.Operation.ADD_VALUE);
	}

	@Override
	public void onEffectStarted(LivingEntity entity, int intensity){
		//The game won't let us reduce max health below 1
		//We check for the case where, were it not for the limit, that would happen
		//And deal the last point of damage
		boolean shouldKill = entity.getMaxHealth() - intensity <= 0;
		super.onEffectStarted(entity, intensity);
		if(entity.getHealth() > entity.getMaxHealth()){
			entity.setHealth(entity.getMaxHealth());
		}
		if(shouldKill){
			entity.hurt(CRMobDamage.damageSource(CRMobDamage.NON_VIABLE, entity.level()), 1);
		}
	}
}

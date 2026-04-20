package com.Da_Technomancer.crossroads.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public class PotionInjectedTrigger extends SimpleCriterionTrigger<PotionInjectedTrigger.TriggerInstance>{

	public static final PotionInjectedTrigger INSTANCE = new PotionInjectedTrigger();

	@Override
	public Codec<TriggerInstance> codec(){
		return TriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player, MobEffect appliedEffect, int intensity, boolean permanent, LivingEntity target){
		trigger(player, triggerInstance -> (!triggerInstance.requirePermanent || permanent) && intensity >= triggerInstance.minIntensity && (triggerInstance.effectDescription.isEmpty() || triggerInstance.effectDescription.get().equals(appliedEffect.getDescriptionId())) && (triggerInstance.target.isEmpty() || triggerInstance.target.get().matches(EntityPredicate.createContext(player, target))));
	}

	public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> target, Optional<String> effectDescription, int minIntensity, boolean requirePermanent) implements SimpleInstance{

		public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
				p_337347_ -> p_337347_.group(
						EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
						EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("target").forGetter(TriggerInstance::target),
						Codec.STRING.optionalFieldOf("potion_effect").forGetter(TriggerInstance::effectDescription),
						Codec.INT.optionalFieldOf("min_intensity", 0).forGetter(TriggerInstance::minIntensity),
						Codec.BOOL.optionalFieldOf("require_permanent", false).forGetter(TriggerInstance::requirePermanent)
				).apply(p_337347_, TriggerInstance::new)
		);
	}
}

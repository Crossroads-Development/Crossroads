package com.Da_Technomancer.crossroads.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class DirtCableTrigger extends SimpleCriterionTrigger<DirtCableTrigger.TriggerInstance>{

	public static final DirtCableTrigger INSTANCE = new DirtCableTrigger();

	@Override
	public Codec<TriggerInstance> codec(){
		return TriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player, int effectId){
		trigger(player, triggerInstance -> triggerInstance.effectId < 0 || effectId == triggerInstance.effectId);
	}

	public static record TriggerInstance(Optional<ContextAwarePredicate> player, int effectId) implements SimpleInstance{

		public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
				p_337347_ -> p_337347_.group(
						EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
						Codec.INT.optionalFieldOf("effect_id", -1).forGetter(TriggerInstance::effectId)
				).apply(p_337347_, TriggerInstance::new)
		);
	}
}

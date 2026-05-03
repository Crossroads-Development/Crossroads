package com.Da_Technomancer.crossroads.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class FluxSinkOverloadTrigger extends SimpleCriterionTrigger<FluxSinkOverloadTrigger.TriggerInstance>{

	public static final FluxSinkOverloadTrigger INSTANCE = new FluxSinkOverloadTrigger();

	@Override
	public Codec<TriggerInstance> codec(){
		return TriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player){
		trigger(player, triggerInstance -> true);
	}

	public static record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleInstance{

		public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
				p_337347_ -> p_337347_.group(
						EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player)
				).apply(p_337347_, TriggerInstance::new)
		);
	}
}

package com.Da_Technomancer.crossroads.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class GolemBuiltTrigger extends SimpleCriterionTrigger<GolemBuiltTrigger.TriggerInstance>{

	public static final GolemBuiltTrigger INSTANCE = new GolemBuiltTrigger();

	@Override
	public Codec<TriggerInstance> codec(){
		return TriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player, int distance, String beamSourceType){
		trigger(player, triggerInstance -> distance >= triggerInstance.minDistance && (triggerInstance.beamSourceType.isBlank() || beamSourceType.equals(triggerInstance.beamSourceType)));
	}

	public static record TriggerInstance(Optional<ContextAwarePredicate> player, int minDistance, String beamSourceType) implements SimpleInstance{

		public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
				p_337347_ -> p_337347_.group(
						EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
						Codec.INT.fieldOf("min_distance").forGetter(TriggerInstance::minDistance),
						Codec.STRING.optionalFieldOf("beam_source_type", "").forGetter(TriggerInstance::beamSourceType)
				).apply(p_337347_, TriggerInstance::new)
		);
	}
}

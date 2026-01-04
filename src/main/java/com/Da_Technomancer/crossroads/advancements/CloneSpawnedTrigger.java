package com.Da_Technomancer.crossroads.advancements;

import com.Da_Technomancer.crossroads.api.witchcraft.EntityTemplate;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;

import java.util.Optional;

public class CloneSpawnedTrigger extends SimpleCriterionTrigger<CloneSpawnedTrigger.TriggerInstance>{

	public static final CloneSpawnedTrigger INSTANCE = new CloneSpawnedTrigger();

	@Override
	public Codec<TriggerInstance> codec(){
		return TriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player, EntityTemplate spawnedTemplate, boolean isNonViable){
		trigger(player, triggerInstance -> spawnedTemplate.quality() >= triggerInstance.minQuality && spawnedTemplate.totalComplexity() >= triggerInstance.minComplexity && spawnedTemplate.totalSoulComplexity() >= triggerInstance.minSoulComplexity && triggerInstance.viableRequirement.matches(isNonViable) && (triggerInstance.entityType.isBlank() || triggerInstance.entityType.equals(spawnedTemplate.entityID().toString())));
	}

	public static record TriggerInstance(Optional<ContextAwarePredicate> player, int minQuality, int minComplexity, int minSoulComplexity, ViableRequirement viableRequirement, String entityType) implements SimpleInstance{

		public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
				p_337347_ -> p_337347_.group(
						EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
						Codec.INT.optionalFieldOf("min_quality", 0).forGetter(TriggerInstance::minQuality),
						Codec.INT.optionalFieldOf("min_complexity", 0).forGetter(TriggerInstance::minComplexity),
						Codec.INT.optionalFieldOf("min_soul_complexity", 0).forGetter(TriggerInstance::minSoulComplexity),
						StringRepresentable.fromEnum(ViableRequirement::values).optionalFieldOf("viable", ViableRequirement.ANY).forGetter(TriggerInstance::viableRequirement),
						Codec.STRING.optionalFieldOf("entity_type", "").forGetter(TriggerInstance::entityType)
				).apply(p_337347_, TriggerInstance::new)
		);
	}

	private static enum ViableRequirement implements StringRepresentable{

		NON_VIABLE("non_viable", false, true),
		VIABLE("viable", true, false),
		ANY("any", true, true);

		private final String name;
		private final boolean matchViable;
		private final boolean matchNonViable;

		ViableRequirement(String name, boolean matchViable, boolean matchNonViable){
			this.name = name;
			this.matchViable = matchViable;
			this.matchNonViable = matchNonViable;
		}

		@Override
		public String getSerializedName(){
			return name;
		}

		public boolean matches(boolean isNonViable){
			return isNonViable && matchNonViable || !isNonViable && matchViable;
		}
	}
}

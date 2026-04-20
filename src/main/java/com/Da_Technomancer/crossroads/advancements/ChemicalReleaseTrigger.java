package com.Da_Technomancer.crossroads.advancements;

import com.Da_Technomancer.crossroads.api.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.api.alchemy.IReagent;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;

import java.util.List;
import java.util.Optional;

public class ChemicalReleaseTrigger extends SimpleCriterionTrigger<ChemicalReleaseTrigger.TriggerInstance>{

	public static final ChemicalReleaseTrigger INSTANCE = new ChemicalReleaseTrigger();

	@Override
	public Codec<TriggerInstance> codec(){
		return TriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player, ReagentMap released){
		trigger(player, triggerInstance -> {
			for(String required : triggerInstance.requiredReagents){
				if(released.getQty(required) <= 0){
					return false;
				}
			}
			if(triggerInstance.requiredPhase.isEmpty()){
				return true;
			}
			EnumMatterPhase requiredPhase = triggerInstance.requiredPhase.get();
			final double temp = released.getTempC();
			for(IReagent reagent : released.keySetReag()){
				if(reagent.getPhase(temp) == requiredPhase){
					return true;
				}
			}
			return false;
		});
	}

	public static record TriggerInstance(Optional<ContextAwarePredicate> player, List<String> requiredReagents, Optional<EnumMatterPhase> requiredPhase) implements SimpleInstance{

		public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
				p_337347_ -> p_337347_.group(
								EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
								Codec.STRING.listOf().fieldOf("required_reagents").forGetter(TriggerInstance::requiredReagents),
								EnumMatterPhase.CODEC.optionalFieldOf("present_phase").forGetter(TriggerInstance::requiredPhase)
						)
						.apply(p_337347_, TriggerInstance::new)
		);
	}

	private static enum VoidRequirement implements StringRepresentable{

		NON_VOID("false", false, true),
		VOID("true", true, false),
		ANY("any", true, true);

		private final String name;
		private final boolean matchVoid;
		private final boolean matchNonVoid;

		VoidRequirement(String name, boolean matchVoid, boolean matchNonVoid){
			this.name = name;
			this.matchVoid = matchVoid;
			this.matchNonVoid = matchNonVoid;
		}

		@Override
		public String getSerializedName(){
			return name;
		}

		public boolean matches(boolean isVoid){
			return isVoid && matchVoid || !isVoid && matchNonVoid;
		}
	}
}

package com.Da_Technomancer.crossroads.advancements;

import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;

import java.util.Optional;

public class BeamAlignmentTrigger extends SimpleCriterionTrigger<BeamAlignmentTrigger.TriggerInstance>{

	public static final BeamAlignmentTrigger INSTANCE = new BeamAlignmentTrigger();

	@Override
	public Codec<TriggerInstance> codec(){
		return TriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player, EnumBeamAlignments alignment, boolean isVoid){
		trigger(player, triggerInstance -> triggerInstance.matches(alignment, isVoid));
	}

	public static record TriggerInstance(Optional<ContextAwarePredicate> player, EnumBeamAlignments alignment, VoidRequirement voidRequirement) implements SimpleCriterionTrigger.SimpleInstance{

		public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
				p_337347_ -> p_337347_.group(
								EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
								EnumBeamAlignments.CODEC.fieldOf("alignment").forGetter(TriggerInstance::alignment),
								StringRepresentable.fromEnum(VoidRequirement::values).optionalFieldOf("void", VoidRequirement.ANY).forGetter(TriggerInstance::voidRequirement)
						)
						.apply(p_337347_, TriggerInstance::new)
		);

		public boolean matches(EnumBeamAlignments alignment, boolean isVoid){
			return this.alignment == alignment && voidRequirement().matches(isVoid);
		}
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

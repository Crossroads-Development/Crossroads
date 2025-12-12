package com.Da_Technomancer.crossroads.effects.entity_modifiers;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.witchcraft.SimpleEntityModifierType;
import com.Da_Technomancer.crossroads.api.witchcraft.IEntityModifier;
import com.Da_Technomancer.crossroads.api.witchcraft.IEntityModifierType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public record PatientEntityModifier(int complexity, int soulComplexity) implements IEntityModifier{

	public static final IEntityModifierType<PatientEntityModifier> TYPE_INSTANCE = new SimpleEntityModifierType<>("patient", PatientEntityModifier::new);

	@Override
	public Entity apply(Entity entity){
		if(entity instanceof Mob mob){
			//prevents despawning
			mob.setPersistenceRequired();
		}
		return entity;
	}

	private static final Component NAME = Component.translatable("ent_mod.patient").withStyle(MiscUtil.TT_DYNAMIC);

	@Override
	public Component getName(@Nullable EntityType<?> entityType, @Nullable Level level){
		return NAME;
	}
}

package com.Da_Technomancer.crossroads.api.witchcraft;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface IEntityModifier{

	int complexity();

	int soulComplexity();

	Entity apply(Entity entity);

	/**
	 * A short-form name for tooltips
	 * @param entityType Entity type. May be null if the entity is unregistered
	 * @param level World
	 * @return A short-form name.
	 */
	Component getName(@Nullable EntityType<?> entityType, @Nullable Level level);
}

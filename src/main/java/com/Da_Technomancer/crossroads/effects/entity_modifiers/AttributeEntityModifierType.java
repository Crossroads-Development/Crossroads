package com.Da_Technomancer.crossroads.effects.entity_modifiers;

import com.Da_Technomancer.crossroads.api.witchcraft.LevelsEntityModifierType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.BiFunction;

public class AttributeEntityModifierType extends LevelsEntityModifierType{

	public AttributeEntityModifierType(String id, Component baseDescription, Pair<Holder<Attribute>, AttributeModifier>[][] attributeModifiersByLevel){
		super(id, attributeModifiersByLevel.length, makeAttributeFunction(attributeModifiersByLevel), baseDescription);
	}

	private static BiFunction<Entity, Integer, Entity> makeAttributeFunction(Pair<Holder<Attribute>, AttributeModifier>[][] attributeModifiersByLevel){
		return (Entity e, Integer lvl) -> {
			if(e instanceof LivingEntity living){
				AttributeMap attributeMap = living.getAttributes();
				for(Pair<Holder<Attribute>, AttributeModifier> attributeModifier : attributeModifiersByLevel[lvl - 1]){
					AttributeInstance inst = attributeMap.getInstance(attributeModifier.getLeft());
					if(inst != null){
						inst.addPermanentModifier(attributeModifier.getRight());
					}
				}
			}
			return e;
		};
	}
}

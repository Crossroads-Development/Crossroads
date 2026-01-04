package com.Da_Technomancer.crossroads.effects.entity_modifiers;

import com.Da_Technomancer.crossroads.api.witchcraft.SimpleEntityModifierType;
import com.Da_Technomancer.crossroads.api.witchcraft.IEntityModifier;
import com.Da_Technomancer.crossroads.api.witchcraft.IEntityModifierType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public record NamedEntityModifier(int complexity, int soulComplexity, String name) implements IEntityModifier{

	private static final Codec<IEntityModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.INT.fieldOf("complexity").forGetter(IEntityModifier::complexity), Codec.INT.fieldOf("soul_complexity").forGetter(IEntityModifier::soulComplexity), Codec.STRING.fieldOf("name").forGetter((IEntityModifier entMod) -> entMod instanceof NamedEntityModifier imEntMod ? imEntMod.name() : "")).apply(instance, NamedEntityModifier::new));
	private static final StreamCodec<ByteBuf, IEntityModifier> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, IEntityModifier::complexity, ByteBufCodecs.VAR_INT, IEntityModifier::soulComplexity, ByteBufCodecs.STRING_UTF8, (IEntityModifier entMod) -> entMod instanceof NamedEntityModifier imEntMod ? imEntMod.name() : "", NamedEntityModifier::new);

	public static final IEntityModifierType<NamedEntityModifier> TYPE_INSTANCE = new SimpleEntityModifierType<>("named", NamedEntityModifier::new, CODEC, STREAM_CODEC);

	public NamedEntityModifier(ItemStack craftingStack, int complexity, int soulComplexity){
		this(complexity, soulComplexity, craftingStack.getOrDefault(DataComponents.CUSTOM_NAME, Component.empty()).getString());
	}

	@Override
	public Entity apply(Entity entity){
		if(name == null || name.isBlank()){
			return entity;
		}
		entity.setCustomName(Component.literal(name));
		return entity;
	}

	@Override
	public Component getName(@Nullable EntityType<?> entityType, @Nullable Level level){
		if(name != null && !name.isBlank()){
			return Component.translatable("ent_mod.named.named", name);
		}
		return Component.translatable("ent_mod.named.unnamed");
	}
}

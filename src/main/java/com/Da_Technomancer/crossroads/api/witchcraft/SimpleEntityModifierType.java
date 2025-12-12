package com.Da_Technomancer.crossroads.api.witchcraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

public class SimpleEntityModifierType<T extends IEntityModifier> implements IEntityModifierType<T>{

	private final String id;
	private final BespokeModifierFactory<T> makeFromItem;
	private final Codec<IEntityModifier> codec;
	private final StreamCodec<ByteBuf, IEntityModifier> streamCodec;

	public SimpleEntityModifierType(String id, SimpleModifierFactory<T> makeGeneric){
		this.id = id;
		this.makeFromItem = makeGeneric;
		this.codec = RecordCodecBuilder.create(instance -> instance.group(Codec.INT.fieldOf("complexity").forGetter(IEntityModifier::complexity), Codec.INT.fieldOf("soul_complexity").forGetter(IEntityModifier::soulComplexity)).apply(instance, makeGeneric::apply));
		this.streamCodec = StreamCodec.composite(ByteBufCodecs.VAR_INT, IEntityModifier::complexity, ByteBufCodecs.VAR_INT, IEntityModifier::soulComplexity, makeGeneric::apply);
	}

	public SimpleEntityModifierType(String id, BespokeModifierFactory<T> makeFromItem, Codec<IEntityModifier> codec, StreamCodec<ByteBuf, IEntityModifier> streamCodec){
		this.id = id;
		this.makeFromItem = makeFromItem;
		this.codec = codec;
		this.streamCodec = streamCodec;
	}

	@Nonnull
	@Override
	public String getModifierID(){
		return id;
	}

	@Override
	public Codec<IEntityModifier> modifierCodec(){
		return codec;
	}

	@Override
	public StreamCodec<ByteBuf, IEntityModifier> modifierStreamCodec(){
		return streamCodec;
	}

	@Nonnull
	@Override
	public T mergeModifiers(@Nonnull IEntityModifier oldModifier, @Nonnull IEntityModifier newModifier){
		return (T) newModifier;
	}

	@Nullable
	@Override
	public T createModifier(ItemStack craftingItem, int complexity, int soulComplexity){
		return makeFromItem.apply(craftingItem, complexity, soulComplexity);
	}

	public static interface SimpleModifierFactory<T extends IEntityModifier> extends BiFunction<Integer, Integer, T>, BespokeModifierFactory<T>{

		@Override
		T apply(Integer complexity, Integer soulComplexity);

		@Override
		default T apply(ItemStack craftingStack, Integer complexity, Integer soulComplexity){
			return apply(complexity, soulComplexity);
		}
	}

	public static interface BespokeModifierFactory<T extends IEntityModifier>{

		T apply(ItemStack craftingStack, Integer complexity, Integer soulComplexity);
	}
}

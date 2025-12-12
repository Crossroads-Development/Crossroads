package com.Da_Technomancer.crossroads.api.witchcraft;

import com.Da_Technomancer.crossroads.crafting.EmbryoLabModifierRec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class represents a category of modifiers that could be applied to an entity (for cloning)
 * Any custom data for the individual modification being applied is the IEntityModifier
 * Most 'simple' modifier types would use SimpleEntityModifierType
 * Or LevelsEntityModifierType for when there are a few integer levels of the modifier
 * New instances of IEntityModifierType need to be registered in EmbryoLabModifierRec::registerEffect
 * New instances of IEntityModifier don't need to be registered as long as there's an associated type
 * @param <T> The subclass of IEntityModifier associated with this type
 */
public interface IEntityModifierType<T extends IEntityModifier>{

	public static final Codec<IEntityModifierType<?>> CODEC = Codec.STRING.comapFlatMap(id -> {
		IEntityModifierType<?> result = EmbryoLabModifierRec.lookupModifierType(id);
		if(result == null){
			return DataResult.error(() -> "Invalid entity modifier type id {" + id + "}");
		}
		return DataResult.success(result);
	}, IEntityModifierType::getModifierID);

	public static final StreamCodec<ByteBuf, IEntityModifierType<?>> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(EmbryoLabModifierRec::lookupModifierType, IEntityModifierType::getModifierID);

	/**
	 * @return A unique string id identifying this type. Used for serialization, so keep it short.
	 */
	@Nonnull
	String getModifierID();

	/**
	 * Treat this as if the return type was {@code Codec<T>}
	 * @return A codec which can handle anything which might be returned by this instance's mergeModifiers or createModifier
	 */
	Codec<IEntityModifier> modifierCodec();

	/**
	 * Treat this as if the return type was {@code StreamCodec<ByteBuf, T>}
	 * @return A stream codec which can handle anything which might be returned by this instance's mergeModifiers or createModifier
	 */
	StreamCodec<ByteBuf, IEntityModifier> modifierStreamCodec();

	/**
	 * Used when overwriting an existing IEntityModifier of the same type
	 * Allows combining the two modifiers
	 * @param oldModifier The old value
	 * @param newModifier The newly applied value. Only pass instances of type {@code T}
	 * @return The result of overwriting the existing modifier that should be applied instead
	 */
	@Nonnull
	T mergeModifiers(@Nonnull IEntityModifier oldModifier, @Nonnull IEntityModifier newModifier);

	/**
	 * Gets the associated IEntityModifier when crafting this modifier
	 * If null, this refuses to generate a modifier from this item
	 * If craftingItem is Itemstack.EMPTY, this is for JEI integration, don't return null
	 * @param craftingItem The itemstack being inserted into the crafting recipe. May be empty.
	 * @param complexity Complexity of the created modifier
	 * @param soulComplexity Soul complexity of the created modifier
	 * @return The IEntityModifier instance
	 */
	@Nullable
	T createModifier(ItemStack craftingItem, int complexity, int soulComplexity);
}

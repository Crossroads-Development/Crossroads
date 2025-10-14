package com.Da_Technomancer.crossroads.api.crafting;

import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public interface IOptionalRecipe<T extends RecipeInput> extends Recipe<T>{

	// TODO: Determine if changed inherited param "level" should remain unused
	@Override
	default @NotNull ItemStack assemble(T recipeInput, HolderLookup.Provider var2){
		return assemble(recipeInput);
	}

	/**
	 * Gets the created itemstack
	 * Safe to modify.
	 * Some recipes may return different items for assemble vs getResultItem- trust assemble
	 */
	default ItemStack assemble(T recipeInput){
		ItemStack result = getResultItem();
		if(result.isEmpty()){
			return result;
		}else{
			return result.copy();
		}
	}

	// TODO: Determine if changed inherited param "provider" should remain unused
	@Override
	default @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider provider){
		return getResultItem();
	}

	/**
	 * Gets the created itemstack
	 * DO NOT MODIFY THE RETURNED ITEMSTACK
	 * Some recipes may return different items for assemble vs getResultItem- trust assemble
	 */
	ItemStack getResultItem();

	/**
	 * Whether this recipe should be considered "real". If not, ignore it.
	 * This exists to help players disable recipes with data packs- the vanilla method is to set output to air
	 * @return Whether this recipe is active
	 */
	boolean isEnabled();

	/**
	 * Whether this recipe is either a custom recipe category or has special behavior
	 * Disables recipe book support; however the recipe book doesn't work for custom recipe categories anyway
	 * If this is false and a custom recipe, the recipe book logs errors at startup
	 * @return Whether this recipe is either a custom recipe category or has special behavior
	 */
	@Override
	default boolean isSpecial(){
		return true;
	}

	/**
	 * Given a Codec for this an enabled version of this recipe which maps everything EXCEPT the enabled flag, adds enable/disable functionality
	 * @param baseCodec Codec for the IOptionalRecipe subclass which doesn't include the isEnabled field
	 * @param disabledInstance Singleton instance which is disabled
	 * @return Modified codec which handles active/disabled recipe functionality
	 * @param <S> The IOptionalRecipe subclass
	 */
	public static <S extends IOptionalRecipe<?>> Codec<S> codecWithDisable(Codec<S> baseCodec, S disabledInstance){
		return new OptionalRecipeCodec<>(disabledInstance, baseCodec);
	}

	/**
	 * Given a MapCodec for this an enabled version of this recipe which maps everything EXCEPT the enabled flag, adds enable/disable functionality
	 * @param baseCodec Codec for the IOptionalRecipe subclass which doesn't include the isEnabled field
	 * @param disabledInstance Singleton instance which is disabled
	 * @return Modified codec which handles active/disabled recipe functionality
	 * @param <S> The IOptionalRecipe subclass
	 */
	public static <S extends IOptionalRecipe<?>> MapCodec<S> codecWithDisable(MapCodec<S> baseCodec, S disabledInstance){
		return new OptionalRecipeMapCodec<>(disabledInstance, baseCodec);
	}

	/**
	 * Given a StreamCodec for this an enabled version of this recipe which maps everything EXCEPT the enabled flag, adds enable/disable functionality
	 * @param baseCodec StreamCodec for the IOptionalRecipe subclass which doesn't include the isEnabled field
	 * @param disabledInstance Singleton instance which is disabled
	 * @return Modified StreamCodec which handles active/disabled recipe functionality
	 * @param <S> The IOptionalRecipe subclass
	 */
	public static <S extends IOptionalRecipe<?>> StreamCodec<RegistryFriendlyByteBuf, S> codecWithDisable(StreamCodec<RegistryFriendlyByteBuf, S> baseCodec, S disabledInstance){
		return StreamCodec.of(new StreamEncoder<RegistryFriendlyByteBuf, S>(){
			@Override
			public void encode(RegistryFriendlyByteBuf byteBuf, S value){
				if(value.isEnabled()){
					byteBuf.writeBoolean(true);
					baseCodec.encode(byteBuf, value);
				}else{
					byteBuf.writeBoolean(false);
				}
			}
		}, (byteBuf) -> byteBuf.readBoolean() ? baseCodec.decode(byteBuf) : disabledInstance);
	}

	public static class OptionalRecipeCodec<S extends IOptionalRecipe<?>> implements Codec<S>{

		private static final Codec<Boolean> ENABLED_CODEC = Codec.BOOL.optionalFieldOf("active", true).codec();
		private final S disabledInstance;
		private final Codec<S> baseCodec;

		private OptionalRecipeCodec(S disabledInstance, Codec<S> baseCodec){
			this.disabledInstance = disabledInstance;
			this.baseCodec = baseCodec;
		}

		@Override
		public <T1> DataResult<Pair<S, T1>> decode(DynamicOps<T1> ops, T1 input){
			return ENABLED_CODEC.decode(ops, input).getOrThrow().getFirst() ? baseCodec.decode(ops, input) : DataResult.success(Pair.of(disabledInstance, input));
		}

		@Override
		public <T1> DataResult<T1> encode(S input, DynamicOps<T1> ops, T1 prefix){
			if(input.isEnabled()){
				return ENABLED_CODEC.encode(true, ops, prefix).flatMap(f -> baseCodec.encode(input, ops, f));
			}else{
				return ENABLED_CODEC.encode(false, ops, prefix);
			}
		}
	}

	public static class OptionalRecipeMapCodec<S extends IOptionalRecipe<?>> extends MapCodec<S>{

		private static final MapCodec<Boolean> ENABLED_CODEC = Codec.BOOL.optionalFieldOf("active", true);
		private final S disabledInstance;
		private final MapCodec<S> baseCodec;

		private OptionalRecipeMapCodec(S disabledInstance, MapCodec<S> baseCodec){
			this.disabledInstance = disabledInstance;
			this.baseCodec = baseCodec;
		}

		@Override
		public <T> Stream<T> keys(DynamicOps<T> ops){
			return Streams.concat(baseCodec.keys(ops), ENABLED_CODEC.keys(ops));
		}

		@Override
		public <T> DataResult<S> decode(DynamicOps<T> ops, MapLike<T> input){
			return ENABLED_CODEC.decode(ops, input).getOrThrow() ? baseCodec.decode(ops, input) : DataResult.success(disabledInstance);
		}

		@Override
		public <T> RecordBuilder<T> encode(S input, DynamicOps<T> ops, RecordBuilder<T> prefix){
			if(input.isEnabled()){
				//There's something dirty in some of the encode implementations that seems to wipe some things written to ops up to that point. Therefore, applying the ENABLED_CODEC (which doesn't do that) second.
				baseCodec.encode(input, ops, prefix);
				return ENABLED_CODEC.encode(true, ops, prefix);
			}else{
				return ENABLED_CODEC.encode(false, ops, prefix);
			}
		}
	}
}

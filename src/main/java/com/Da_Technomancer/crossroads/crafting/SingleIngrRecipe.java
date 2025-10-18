package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public abstract class SingleIngrRecipe implements IOptionalRecipe<RecipeInput>{

	protected final Ingredient ingredient;
	protected final ItemStack result;
	protected final String group;
	protected final boolean active;

	protected SingleIngrRecipe(){
		ingredient = Ingredient.EMPTY;
		result = ItemStack.EMPTY;
		group = "";
		active = false;
	}

	protected SingleIngrRecipe(String group, Ingredient ingredient, ItemStack result){
		this.group = group;
		this.ingredient = ingredient;
		this.result = result;
		this.active = true;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	/**
	 * Recipes with equal group are combined into one button in the recipe book
	 */
	@Override
	public String getGroup(){
		return group;
	}

	/**
	 * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
	 * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
	 */
	@Override
	public ItemStack getResultItem(){
		return result;
	}

	public Ingredient getIngredient(){
		return ingredient;
	}

	@Override
	public NonNullList<Ingredient> getIngredients(){
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(ingredient);
		return nonnulllist;
	}

	@Override
	public boolean matches(RecipeInput input, Level worldIn){
		return isEnabled() && ingredient.test(input.getItem(0));
	}

	/**
	 * Used to determine if this recipe can fit in a grid of the given width/height
	 */
	@Override
	public boolean canCraftInDimensions(int width, int height){
		return width != 0 && height != 0;
	}

	public static class SingleRecipeSerializer<T extends SingleIngrRecipe> implements RecipeSerializer<T>{

		private final MapCodec<T> CODEC;
		private final StreamCodec<RegistryFriendlyByteBuf, T> STREAM_CODEC;

		public SingleRecipeSerializer(IRecipeFactory<T> factory, T disabledRecipe){
			MapCodec<T> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
					CraftingUtil.recipeGroupFieldCodec().forGetter(SingleIngrRecipe::getGroup),
					CraftingUtil.itemIngredientMapCodec("ingredient", false).forGetter(SingleIngrRecipe::getIngredient),
					CraftingUtil.itemStackMapCodec("output", true).forGetter(SingleIngrRecipe::getResultItem)
			).apply(instance, factory));
			CODEC = IOptionalRecipe.codecWithDisable(codec, disabledRecipe);
			StreamCodec<RegistryFriendlyByteBuf, T> streamCodec = StreamCodec.composite(
					ByteBufCodecs.STRING_UTF8, SingleIngrRecipe::getGroup,
					Ingredient.CONTENTS_STREAM_CODEC, SingleIngrRecipe::getIngredient,
					ItemStack.STREAM_CODEC, SingleIngrRecipe::getResultItem,
					factory
			);
			STREAM_CODEC = IOptionalRecipe.codecWithDisable(streamCodec, disabledRecipe);
		}

		@Override
		public MapCodec<T> codec(){
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec(){
			return STREAM_CODEC;
		}

		public interface IRecipeFactory<T extends SingleIngrRecipe> extends Function3<String, Ingredient, ItemStack, T>{

		}
	}
}
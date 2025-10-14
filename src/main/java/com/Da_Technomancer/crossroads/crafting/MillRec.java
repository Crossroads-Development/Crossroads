package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.mojang.serialization.Codec;
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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class MillRec implements IOptionalRecipe<RecipeInput>{

	private final String group;
	private final Ingredient ingr;
	private final List<ItemStack> outputs;
	private final boolean active;

	private MillRec(){
		group = "";
		ingr = Ingredient.EMPTY;
		outputs = List.of();
		active = false;
	}

	/**
	 *
	 * @param name Recipe group
	 * @param input Input ingredient
	 * @param output Maximum of 3 ItemStacks
	 */
	private MillRec(String name, Ingredient input, List<ItemStack> output){
		group = name;
		ingr = input;
		outputs = output;
		this.active = false;
	}

	/**
	 * This recipe has up to 3 outputs. This method should be used in place of getCraftingReuslt or getRecipeOutput
	 * @return An array of up to 3 created ItemStacks
	 */
	public List<ItemStack> getOutputs(){
		return outputs;
	}

	@Override
	public NonNullList<Ingredient> getIngredients(){
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(ingr);
		return nonnulllist;
	}

	public Ingredient getIngredient(){
		return ingr;
	}

	@Override
	public boolean matches(RecipeInput input, Level worldIn){
		return ingr.test(input.getItem(0));
	}

	@Override
	public boolean canCraftInDimensions(int width, int height){
		return true;
	}

	@Override
	public ItemStack getResultItem(){
		return outputs.size() != 0 ? outputs.get(0).copy() : ItemStack.EMPTY;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRBlocks.millstone);
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.MILL_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.MILL_TYPE;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	public static class Serializer implements RecipeSerializer<MillRec>{

		static{
			MapCodec<MillRec> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
					CraftingUtil.recipeGroupFieldCodec().forGetter(MillRec::getGroup),
					CraftingUtil.itemIngredientMapCodec("input", false).forGetter(MillRec::getIngredient),
					Codec.withAlternative(ItemStack.CODEC.listOf(1, 3), ItemStack.CODEC.xmap(List::of, stackList -> stackList.isEmpty() ? ItemStack.EMPTY : stackList.get(0))).fieldOf("output").forGetter(MillRec::getOutputs)
			).apply(instance, MillRec::new));

			StreamCodec<RegistryFriendlyByteBuf, MillRec> streamCodec = StreamCodec.composite(
					ByteBufCodecs.STRING_UTF8, MillRec::getGroup,
					Ingredient.CONTENTS_STREAM_CODEC, MillRec::getIngredient,
					ByteBufCodecs.collection(ArrayList::new, ItemStack.STREAM_CODEC), MillRec::getOutputs,
					MillRec::new
			);
			MillRec disabledRec = new MillRec();
			CODEC = IOptionalRecipe.codecWithDisable(codec, disabledRec);
			STREAM_CODEC = IOptionalRecipe.codecWithDisable(streamCodec, disabledRec);
		}

		public static final MapCodec<MillRec> CODEC;
		public static final StreamCodec<RegistryFriendlyByteBuf, MillRec> STREAM_CODEC;

		@Override
		public MapCodec<MillRec> codec(){
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, MillRec> streamCodec(){
			return STREAM_CODEC;
		}
	}
}

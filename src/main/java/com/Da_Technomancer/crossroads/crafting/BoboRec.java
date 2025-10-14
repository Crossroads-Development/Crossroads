package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.items.CRItems;
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

public class BoboRec implements IOptionalRecipe<RecipeInput>{

	private final String group;
	private final Ingredient[] ingr;
	private final ItemStack output;
	private final boolean active;

	private BoboRec(){
		group = "";
		ingr = new Ingredient[0];
		output = ItemStack.EMPTY;
		active = false;
	}

	private BoboRec(String name, Ingredient inputA, Ingredient inputB, Ingredient inputC, ItemStack output){
		group = name;
		ingr = new Ingredient[] {inputA, inputB, inputC};
		this.output = output;
		this.active = true;
	}

	@Override
	public boolean matches(RecipeInput input, Level worldIn){
		if(!isEnabled() || input.size() != 3){
			return false;
		}
		//Known issue: this will pass if one input meets 2+ ingredients, even if the third input is irrelevant
		//No default Crossroads recipes have this issue- it would be silly to add a recipe that does
		for(Ingredient ingredient : ingr){
			boolean pass = false;
			for(int i = 0; i < 3; i++){
				if(ingredient.test(input.getItem(i))){
					pass = true;
					break;
				}
			}
			if(!pass){
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	@Override
	public NonNullList<Ingredient> getIngredients(){
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(ingr[0]);
		nonnulllist.add(ingr[1]);
		nonnulllist.add(ingr[2]);
		return nonnulllist;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height){
		return true;
	}

	@Override
	public ItemStack getResultItem(){
		return output;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRItems.boboRod);
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.BOBO_SERIAL;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.BOBO_TYPE;
	}

	public static class Serializer implements RecipeSerializer<BoboRec>{

		static{
			BoboRec disabledRec = new BoboRec();

			MapCodec<BoboRec> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
					CraftingUtil.recipeGroupFieldCodec().forGetter(BoboRec::getGroup),
					CraftingUtil.itemIngredientMapCodec("input_a", false).forGetter(boboRec -> boboRec.ingr[0]),
					CraftingUtil.itemIngredientMapCodec("input_b", false).forGetter(boboRec -> boboRec.ingr[1]),
					CraftingUtil.itemIngredientMapCodec("input_c", false).forGetter(boboRec -> boboRec.ingr[2]),
					CraftingUtil.itemStackMapCodec("output", false).forGetter(BoboRec::getResultItem)
			).apply(instance, BoboRec::new));
			CODEC = IOptionalRecipe.codecWithDisable(codec, disabledRec);

			StreamCodec<RegistryFriendlyByteBuf, BoboRec> streamCodec = StreamCodec.composite(
					ByteBufCodecs.STRING_UTF8, BoboRec::getGroup,
					Ingredient.CONTENTS_STREAM_CODEC, boboRec -> boboRec.ingr[0],
					Ingredient.CONTENTS_STREAM_CODEC, boboRec -> boboRec.ingr[1],
					Ingredient.CONTENTS_STREAM_CODEC, boboRec -> boboRec.ingr[2],
					ItemStack.STREAM_CODEC, BoboRec::getResultItem,
					BoboRec::new
			);
			STREAM_CODEC = IOptionalRecipe.codecWithDisable(streamCodec, disabledRec);
		}

		public static final MapCodec<BoboRec> CODEC;
		public static final StreamCodec<RegistryFriendlyByteBuf, BoboRec> STREAM_CODEC;

		@Override
		public MapCodec<BoboRec> codec(){
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, BoboRec> streamCodec(){
			return STREAM_CODEC;
		}
	}
}

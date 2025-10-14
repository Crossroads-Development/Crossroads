package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
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
import net.neoforged.neoforge.fluids.FluidStack;

public class CrucibleRec implements IOptionalRecipe<RecipeInput>{

	private final String group;

	private final Ingredient input;
	private final FluidStack output;
	private final boolean active;

	private CrucibleRec(){
		group = "";
		input = Ingredient.EMPTY;
		output = FluidStack.EMPTY;
		active = false;
	}

	private CrucibleRec(String name, Ingredient input, FluidStack output){
		group = name;
		this.input = input;
		this.output = output;
		this.active = true;
	}

	public FluidStack getOutput(){
		return output;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	@Override
	public boolean matches(RecipeInput inv, Level worldIn){
		return active && input.test(inv.getItem(0));
	}

	@Override
	public boolean canCraftInDimensions(int width, int height){
		return true;
	}

	@Override
	public ItemStack getResultItem(){
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRBlocks.heatingCrucible);
	}

	@Override
	public NonNullList<Ingredient> getIngredients(){
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(input);
		return nonnulllist;
	}

	public Ingredient getIngredient(){
		return input;
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.CRUCIBLE_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.CRUCIBLE_TYPE;
	}

	public static class Serializer implements RecipeSerializer<CrucibleRec>{

		static{
			MapCodec<CrucibleRec> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
					CraftingUtil.recipeGroupFieldCodec().forGetter(CrucibleRec::getGroup),
					CraftingUtil.itemIngredientMapCodec("input", false).forGetter(CrucibleRec::getIngredient),
					CraftingUtil.fluidStackMapCodec("output", false).forGetter(CrucibleRec::getOutput)
			).apply(instance, CrucibleRec::new));
			StreamCodec<RegistryFriendlyByteBuf, CrucibleRec> streamCodec = StreamCodec.composite(
					ByteBufCodecs.STRING_UTF8, CrucibleRec::getGroup,
					Ingredient.CONTENTS_STREAM_CODEC, CrucibleRec::getIngredient,
					FluidStack.STREAM_CODEC, CrucibleRec::getOutput,
					CrucibleRec::new
			);
			CrucibleRec disabledRec = new CrucibleRec();
			CODEC = IOptionalRecipe.codecWithDisable(codec, disabledRec);
			STREAM_CODEC = IOptionalRecipe.codecWithDisable(streamCodec, disabledRec);
		}

		// String name, Ingredient input, FluidStack output, boolean active
		private static final MapCodec<CrucibleRec> CODEC;

		private static final StreamCodec<RegistryFriendlyByteBuf, CrucibleRec> STREAM_CODEC;

		@Override
		public MapCodec<CrucibleRec> codec(){
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, CrucibleRec> streamCodec(){
			return STREAM_CODEC;
		}
	}
}

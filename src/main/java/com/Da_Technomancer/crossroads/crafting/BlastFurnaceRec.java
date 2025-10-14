package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public class BlastFurnaceRec implements IOptionalRecipe<RecipeInput>{

	private final String group;
	private final Ingredient ingr;

	private final FluidStack output;
	private final int slag;

	private final boolean active;

	private BlastFurnaceRec(){
		this.active = false;
		group = "";
		ingr = Ingredient.EMPTY;
		output = FluidStack.EMPTY;
		slag = 0;
	}

	private BlastFurnaceRec(String name, Ingredient input, FluidStack output, int slag){
		group = name;
		ingr = input;
		this.output = output;
		this.slag = slag;
		this.active = true;
	}

	public FluidStack getOutput(){
		return output;
	}

	public int getSlag(){
		return slag;
	}

	@Override
	public boolean matches(RecipeInput input, Level worldIn){
		return isEnabled() && ingr.test(input.getItem(0));
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
	public boolean canCraftInDimensions(int width, int height){
		return true;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	@Override
	public ItemStack getResultItem(){
		return new ItemStack(CRItems.slag, slag);
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRBlocks.blastFurnace);
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.BLAST_FURNACE_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.BLAST_FURNACE_TYPE;
	}

	public static class Serializer implements RecipeSerializer<BlastFurnaceRec>{

		static{
			BlastFurnaceRec disabledRec = new BlastFurnaceRec();
			MapCodec<BlastFurnaceRec> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
					CraftingUtil.recipeGroupFieldCodec().forGetter(BlastFurnaceRec::getGroup),
					CraftingUtil.itemIngredientMapCodec("ingredient", false).forGetter(BlastFurnaceRec::getIngredient),
					CraftingUtil.fluidStackMapCodec("output", false).forGetter(BlastFurnaceRec::getOutput),
					ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("slag", 0).forGetter(BlastFurnaceRec::getSlag)
			).apply(instance, BlastFurnaceRec::new));
			CODEC = IOptionalRecipe.codecWithDisable(codec, disabledRec);
			StreamCodec<RegistryFriendlyByteBuf, BlastFurnaceRec> streamCodec = StreamCodec.composite(
					ByteBufCodecs.STRING_UTF8, BlastFurnaceRec::getGroup,
					Ingredient.CONTENTS_STREAM_CODEC, BlastFurnaceRec::getIngredient,
					FluidStack.STREAM_CODEC, BlastFurnaceRec::getOutput,
					ByteBufCodecs.VAR_INT, BlastFurnaceRec::getSlag,
					BlastFurnaceRec::new
			);
			STREAM_CODEC = IOptionalRecipe.codecWithDisable(streamCodec, disabledRec);
		}

		public static final MapCodec<BlastFurnaceRec> CODEC;
		public static final StreamCodec<RegistryFriendlyByteBuf, BlastFurnaceRec> STREAM_CODEC;

		@Override
		public MapCodec<BlastFurnaceRec> codec(){
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, BlastFurnaceRec> streamCodec(){
			return STREAM_CODEC;
		}
	}
}

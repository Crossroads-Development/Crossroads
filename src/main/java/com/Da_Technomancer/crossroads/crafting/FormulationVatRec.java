package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.FluidIngredient;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.witchcraft.FormulationVatTileEntity;
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

public class FormulationVatRec implements IOptionalRecipe<RecipeInput>{

	private final String group;

	private final FluidIngredient input;
	private final int inputQty;
	private final Ingredient itemInput;
	private final FluidStack output;
	private final boolean active;

	private FormulationVatRec(){
		active = false;
		group = "";
		input = FluidIngredient.EMPTY;
		inputQty = 0;
		itemInput = Ingredient.EMPTY;
		output = FluidStack.EMPTY;
	}

	private FormulationVatRec(String name, FluidIngredient input, int inputQty, Ingredient itemInput, FluidStack output){
		group = name;
		this.input = input;
		this.inputQty = inputQty;
		this.itemInput = itemInput;
		this.output = output;
		this.active = true;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	public FluidIngredient getInput(){
		return input;
	}

	public int getInputQty(){
		return inputQty;
	}

	@Override
	public NonNullList<Ingredient> getIngredients(){
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(itemInput);
		return nonnulllist;
	}

	public Ingredient getIngredient(){
		return itemInput;
	}

	public FluidStack getOutput(){
		return output;
	}

	@Override
	public boolean matches(RecipeInput input, Level worldIn){
		if(active && input instanceof FormulationVatTileEntity){
			FormulationVatTileEntity te = (FormulationVatTileEntity) input;
			return itemInput.test(te.getItem(0)) && this.input.test(te.getInputFluid());
		}
		return false;
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
		return new ItemStack(CRBlocks.formulationVat);
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.FORMULATION_VAT_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.FORMULATION_VAT_TYPE;
	}

	public static class Serializer implements RecipeSerializer<FormulationVatRec>{

		static{
			MapCodec<FormulationVatRec> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
					CraftingUtil.recipeGroupFieldCodec().forGetter(FormulationVatRec::getGroup),
					CraftingUtil.fluidIngredientMapCodec("input_fluid", false).forGetter(FormulationVatRec::getInput),
					ExtraCodecs.NON_NEGATIVE_INT.fieldOf("fluid_amount").forGetter(FormulationVatRec::getInputQty),
					CraftingUtil.itemIngredientMapCodec("input_item", false).forGetter(FormulationVatRec::getIngredient),
					CraftingUtil.fluidStackMapCodec("output", false).forGetter(FormulationVatRec::getOutput)
			).apply(instance, FormulationVatRec::new));

			StreamCodec<RegistryFriendlyByteBuf, FormulationVatRec> streamCodec = StreamCodec.composite(
					ByteBufCodecs.STRING_UTF8, FormulationVatRec::getGroup,
					FluidIngredient.STREAM_CODEC, FormulationVatRec::getInput,
					ByteBufCodecs.INT, FormulationVatRec::getInputQty,
					Ingredient.CONTENTS_STREAM_CODEC, FormulationVatRec::getIngredient,
					FluidStack.STREAM_CODEC, FormulationVatRec::getOutput,
					FormulationVatRec::new
			);
			FormulationVatRec disabledRec = new FormulationVatRec();
			CODEC = IOptionalRecipe.codecWithDisable(codec, disabledRec);
			STREAM_CODEC = IOptionalRecipe.codecWithDisable(streamCodec, disabledRec);
		}

		public static final MapCodec<FormulationVatRec> CODEC;
		public static final StreamCodec<RegistryFriendlyByteBuf, FormulationVatRec> STREAM_CODEC;

		@Override
		public MapCodec<FormulationVatRec> codec(){
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, FormulationVatRec> streamCodec(){
			return STREAM_CODEC;
		}
	}
}

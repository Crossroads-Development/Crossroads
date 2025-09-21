package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.FluidIngredient;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.witchcraft.FormulationVatTileEntity;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

public class FormulationVatRec implements IOptionalRecipe<RecipeInput>{

	private final String group;

	private final FluidIngredient input;
	private final int inputQty;
	private final Ingredient itemInput;
	private final FluidStack output;
	private final boolean active;

	public FormulationVatRec(String name, FluidIngredient input, int inputQty, Ingredient itemInput, FluidStack output, boolean active){
		group = name;
		this.input = input;
		this.inputQty = inputQty;
		this.itemInput = itemInput;
		this.output = output;
		this.active = active;
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
		//String name, FluidIngredient input, int inputQty, Ingredient itemInput, FluidStack output, boolean active
		private static final MapCodec<FormulationVatRec> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Codec.STRING.optionalFieldOf("group", "").forGetter(FormulationVatRec::getGroup),
				FluidIngredient.CODEC.fieldOf("input_fluid").forGetter(FormulationVatRec::getInput),
				Codec.INT.fieldOf("fluid_amount").forGetter(FormulationVatRec::getInputQty),
				Ingredient.CODEC.fieldOf("input_item").forGetter(FormulationVatRec::getIngredient),
				FluidStack.CODEC.fieldOf("output").forGetter(FormulationVatRec::getOutput),
				Codec.BOOL.optionalFieldOf("active", true).forGetter(FormulationVatRec::isEnabled)
		).apply(instance, FormulationVatRec::new));

		private static final StreamCodec<RegistryFriendlyByteBuf, FormulationVatRec> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, FormulationVatRec::getGroup,
				FluidIngredient.STREAM_CODEC, FormulationVatRec::getInput,
				ByteBufCodecs.INT, FormulationVatRec::getInputQty,
				Ingredient.CONTENTS_STREAM_CODEC, FormulationVatRec::getIngredient,
				FluidStack.STREAM_CODEC, FormulationVatRec::getOutput,
				ByteBufCodecs.BOOL, FormulationVatRec::isEnabled,
				FormulationVatRec::new
		);

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

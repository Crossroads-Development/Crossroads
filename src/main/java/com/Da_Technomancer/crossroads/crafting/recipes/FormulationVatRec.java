package com.Da_Technomancer.crossroads.crafting.recipes;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.FormulationVatTileEntity;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

public class FormulationVatRec implements IOptionalRecipe<Container>{

	private final ResourceLocation id;
	private final String group;

	private final FluidIngredient input;
	private final int inputQty;
	private final Ingredient itemInput;
	private final FluidStack output;
	private final boolean active;

	public FormulationVatRec(ResourceLocation location, String name, FluidIngredient input, int inputQty, Ingredient itemInput, FluidStack output, boolean active){
		id = location;
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
	public boolean matches(Container inv, Level worldIn){
		if(active && inv instanceof FormulationVatTileEntity){
			FormulationVatTileEntity te = (FormulationVatTileEntity) inv;
			return itemInput.test(te.getItem(0)) && input.test(te.getInputFluid());
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
	public ResourceLocation getId(){
		return id;
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

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<FormulationVatRec>{

		@Override
		public FormulationVatRec fromJson(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = GsonHelper.getAsString(json, "group", "");

			if(!CraftingUtil.isActiveJSON(json)){
				return new FormulationVatRec(recipeId, s, FluidIngredient.EMPTY, 0, Ingredient.EMPTY, FluidStack.EMPTY, false);
			}

			Pair<FluidIngredient, Integer> input = CraftingUtil.getFluidIngredientAndQuantity(json, "input_fluid", false, -1);
			Ingredient inputItem = CraftingUtil.getIngredient(json, "input_item", true);
			FluidStack output = CraftingUtil.getFluidStack(json, "output");
			return new FormulationVatRec(recipeId, s, input.getLeft(), input.getRight(), inputItem, output, true);
		}

		@Nullable
		@Override
		public FormulationVatRec fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
			String s = buffer.readUtf(Short.MAX_VALUE);
			if(!buffer.readBoolean()){//active
				return new FormulationVatRec(recipeId, s, FluidIngredient.EMPTY, 0, Ingredient.EMPTY, FluidStack.EMPTY, false);
			}
			FluidIngredient input = FluidIngredient.readFromBuffer(buffer);
			int fluidQty = buffer.readVarInt();
			Ingredient inputItem = Ingredient.fromNetwork(buffer);
			FluidStack output = FluidStack.readFromPacket(buffer);
			return new FormulationVatRec(recipeId, s, input, fluidQty, inputItem, output, true);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, FormulationVatRec recipe){
			buffer.writeUtf(recipe.getGroup());
			buffer.writeBoolean(recipe.active);

			recipe.input.writeToBuffer(buffer);
			buffer.writeVarInt(recipe.inputQty);
			recipe.itemInput.toNetwork(buffer);
			recipe.output.writeToPacket(buffer);
		}
	}
}

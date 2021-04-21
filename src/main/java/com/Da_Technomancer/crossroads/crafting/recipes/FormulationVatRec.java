package com.Da_Technomancer.crossroads.crafting.recipes;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.FormulationVatTileEntity;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class FormulationVatRec implements IOptionalRecipe<IInventory>{

	private final ResourceLocation id;
	private final String group;

	private final FluidStack input;
	private final Ingredient itemInput;
	private final FluidStack output;
	private final boolean active;

	public FormulationVatRec(ResourceLocation location, String name, FluidStack input, Ingredient itemInput, FluidStack output, boolean active){
		id = location;
		group = name;
		this.input = input;
		this.itemInput = itemInput;
		this.output = output;
		this.active = active;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	public FluidStack getInput(){
		return input;
	}

	@Override
	public NonNullList<Ingredient> getIngredients(){
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(itemInput);
		return nonnulllist;
	}

	public FluidStack getOutput(){
		return output;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn){
		if(active && inv instanceof FormulationVatTileEntity){
			FormulationVatTileEntity te = (FormulationVatTileEntity) inv;
			return itemInput.test(te.getItem(0)) && BlockUtil.sameFluid(input, te.getInputFluid());
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
	public IRecipeSerializer<?> getSerializer(){
		return CRRecipes.FORMULATION_VAT_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public IRecipeType<?> getType(){
		return CRRecipes.FORMULATION_VAT_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FormulationVatRec>{

		@Override
		public FormulationVatRec fromJson(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = JSONUtils.getAsString(json, "group", "");

			if(!CraftingUtil.isActiveJSON(json)){
				return new FormulationVatRec(recipeId, s, FluidStack.EMPTY, Ingredient.EMPTY, FluidStack.EMPTY, false);
			}

			FluidStack input = CraftingUtil.getFluidStack(json, "input_fluid");
			Ingredient inputItem = CraftingUtil.getIngredient(json, "input_item", true);
			FluidStack output = CraftingUtil.getFluidStack(json, "output");
			return new FormulationVatRec(recipeId, s, input, inputItem, output, true);
		}

		@Nullable
		@Override
		public FormulationVatRec fromNetwork(ResourceLocation recipeId, PacketBuffer buffer){
			String s = buffer.readUtf(Short.MAX_VALUE);
			if(!buffer.readBoolean()){//active
				return new FormulationVatRec(recipeId, s, FluidStack.EMPTY, Ingredient.EMPTY, FluidStack.EMPTY, false);
			}
			FluidStack input = FluidStack.readFromPacket(buffer);
			Ingredient inputItem = Ingredient.fromNetwork(buffer);
			FluidStack output = FluidStack.readFromPacket(buffer);
			return new FormulationVatRec(recipeId, s, input, inputItem, output, true);
		}

		@Override
		public void toNetwork(PacketBuffer buffer, FormulationVatRec recipe){
			buffer.writeUtf(recipe.getGroup());
			buffer.writeBoolean(recipe.active);

			recipe.input.writeToPacket(buffer);
			recipe.itemInput.toNetwork(buffer);
			recipe.output.writeToPacket(buffer);
		}
	}
}

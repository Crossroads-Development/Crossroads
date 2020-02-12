package com.Da_Technomancer.crossroads.items.crafting.recipes;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.items.crafting.CRRecipes;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
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

public class CrucibleRec implements IRecipe<IInventory>{

	private final ResourceLocation id;
	private final String group;

	private final Ingredient input;
	private final FluidStack output;

	public CrucibleRec(ResourceLocation location, String name, Ingredient input, FluidStack output){
		id = location;
		group = name;
		this.input = input;
		this.output = output;
	}

	public FluidStack getOutput(){
		return output;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn){
		return input.test(inv.getStackInSlot(0));
	}

	@Override
	public ItemStack getCraftingResult(IInventory inv){
		return getRecipeOutput();
	}

	@Override
	public boolean canFit(int width, int height){
		return true;
	}

	@Override
	public ItemStack getRecipeOutput(){
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getIcon(){
		return new ItemStack(CRBlocks.heatingCrucible);
	}

	@Override
	public NonNullList<Ingredient> getIngredients(){
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(input);
		return nonnulllist;
	}

	@Override
	public ResourceLocation getId(){
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer(){
		return CRRecipes.CRUCIBLE_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public IRecipeType<?> getType(){
		return CRRecipes.CRUCIBLE_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CrucibleRec>{

		@Override
		public CrucibleRec read(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = JSONUtils.getString(json, "group", "");

			Ingredient in = CraftingUtil.getIngredient(json, "input", true);
			FluidStack out = CraftingUtil.getFluidStack(json, "output");
			return new CrucibleRec(recipeId, s, in, out);
		}

		@Nullable
		@Override
		public CrucibleRec read(ResourceLocation recipeId, PacketBuffer buffer){
			String s = buffer.readString(Short.MAX_VALUE);

			Ingredient input = Ingredient.read(buffer);
			FluidStack output = FluidStack.readFromPacket(buffer);
			return new CrucibleRec(recipeId, s, input, output);
		}

		@Override
		public void write(PacketBuffer buffer, CrucibleRec recipe){
			buffer.writeString(recipe.getGroup());

			recipe.input.write(buffer);
			recipe.output.writeToPacket(buffer);
		}
	}
}

package com.Da_Technomancer.crossroads.items.crafting.recipes;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
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

public class BlastFurnaceRec implements IRecipe<IInventory>{

	private final ResourceLocation id;
	private final String group;
	private final Ingredient ingr;

	private final FluidStack output;
	private final int slag;

	public BlastFurnaceRec(ResourceLocation location, String name, Ingredient input, FluidStack output, int slag){
		id = location;
		group = name;
		ingr = input;
		this.output = output;
		this.slag = slag;
	}

	public FluidStack getOutput(){
		return output;
	}

	public int getSlag(){
		return slag;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn){
		return ingr.test(inv.getStackInSlot(0));
	}

	@Override
	public NonNullList<Ingredient> getIngredients(){
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(ingr);
		return nonnulllist;
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
		return new ItemStack(CRItems.slag, slag);
	}

	@Override
	public ItemStack getIcon(){
		return new ItemStack(CRBlocks.blastFurnace);
	}

	@Override
	public ResourceLocation getId(){
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer(){
		return RecipeHolder.BLAST_FURNACE_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public IRecipeType<?> getType(){
		return RecipeHolder.BLAST_FURNACE_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<BlastFurnaceRec>{

		@Override
		public BlastFurnaceRec read(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = JSONUtils.getString(json, "group", "");
			Ingredient ingredient = CraftingUtil.getIngredient(json, "ingredient", false);
			//Output specified as fluid- see CraftingUtil
			FluidStack fluid = CraftingUtil.getFluidStack(json, "output");
			//Slag specified as 1 int tag (default 0)
			int slag = JSONUtils.getInt(json, "slag", 0);
			return new BlastFurnaceRec(recipeId, s, ingredient, fluid, slag);
		}

		@Nullable
		@Override
		public BlastFurnaceRec read(ResourceLocation recipeId, PacketBuffer buffer){
			String s = buffer.readString(Short.MAX_VALUE);
			Ingredient ingredient = Ingredient.read(buffer);
			FluidStack fluid = FluidStack.readFromPacket(buffer);
			int slag = buffer.readVarInt();
			return new BlastFurnaceRec(recipeId, s, ingredient, fluid, slag);
		}

		@Override
		public void write(PacketBuffer buffer, BlastFurnaceRec recipe){
			buffer.writeString(recipe.getGroup());
			recipe.ingr.write(buffer);
			recipe.output.writeToPacket(buffer);
			buffer.writeVarInt(recipe.slag);
		}
	}
}

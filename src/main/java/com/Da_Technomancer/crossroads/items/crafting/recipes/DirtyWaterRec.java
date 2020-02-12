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
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class DirtyWaterRec implements IRecipe<IInventory>{

	private final ResourceLocation id;
	private final String group;
	private final ItemStack stack;
	private final int weight;

	public DirtyWaterRec(ResourceLocation location, String name, ItemStack stack, int weight){
		id = location;
		group = name;
		this.stack = stack;
		this.weight = weight;
	}

	public int getWeight(){
		return weight;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn){
		return true;
	}

	@Override
	public ItemStack getCraftingResult(IInventory inv){
		return getRecipeOutput().copy();
	}

	@Override
	public boolean canFit(int width, int height){
		return true;
	}

	@Override
	public ItemStack getRecipeOutput(){
		return stack;
	}

	@Override
	public ItemStack getIcon(){
		return new ItemStack(CRBlocks.waterCentrifuge);
	}

	@Override
	public ResourceLocation getId(){
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer(){
		return CRRecipes.DIRTY_WATER_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public IRecipeType<?> getType(){
		return CRRecipes.DIRTY_WATER_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<DirtyWaterRec>{

		@Override
		public DirtyWaterRec read(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and output
			String s = JSONUtils.getString(json, "group", "");
			ItemStack output = CraftingUtil.getItemStack(json, "output", true, true);
			int weight = JSONUtils.getInt(json, "weight", 0);
			return new DirtyWaterRec(recipeId, s, output, weight);
		}

		@Nullable
		@Override
		public DirtyWaterRec read(ResourceLocation recipeId, PacketBuffer buffer){
			String s = buffer.readString(Short.MAX_VALUE);
			ItemStack output = buffer.readItemStack();
			int weight = buffer.readInt();
			return new DirtyWaterRec(recipeId, s, output, weight);
		}

		@Override
		public void write(PacketBuffer buffer, DirtyWaterRec recipe){
			buffer.writeString(recipe.getGroup());
			buffer.writeItemStack(recipe.stack);
			buffer.writeInt(recipe.weight);
		}
	}
}

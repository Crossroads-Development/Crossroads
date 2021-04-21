package com.Da_Technomancer.crossroads.crafting.recipes;

import com.Da_Technomancer.crossroads.API.EnumPath;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.CraftingUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class DetailedCrafterRec extends ShapedRecipe implements IOptionalRecipe<CraftingInventory>{

	private final EnumPath path;
	private final boolean active;

	public DetailedCrafterRec(ResourceLocation idIn, String groupIn, EnumPath path, boolean active, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn){
		super(idIn, groupIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn);
		this.path = path;
		this.active = active;
	}

	public EnumPath getPath(){
		return path;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRBlocks.detailedCrafter);
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	@Override
	public IRecipeSerializer<?> getSerializer(){
		return CRRecipes.DETAILED_SERIAL;
	}

	@Override
	public IRecipeType<?> getType(){
		return CRRecipes.DETAILED_TYPE;
	}

	@Override
	public boolean matches(CraftingInventory inv, World world){
		return active && super.matches(inv, world);
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>  implements IRecipeSerializer<DetailedCrafterRec> {

		@Override
		public DetailedCrafterRec fromJson(ResourceLocation recipeId, JsonObject json){
			if(!CraftingUtil.isActiveJSON(json)){
				return new DetailedCrafterRec(recipeId, "", EnumPath.ALCHEMY, false, 0, 0, NonNullList.create(), ItemStack.EMPTY);
			}

			EnumPath path = EnumPath.fromName(JSONUtils.getAsString(json, "path"));
			if(path == null){
				throw new JsonParseException("Invalid path/no path set");
			}

			//Currently, the method of specifying a Detailed Crafter recipe is the same as for a vanilla recipe,
			//Except type is crossroads:detailed_crafter
			//Path is specified as the name of the path with the key "path"
			//And only shaped recipes are supported. No shapeless recipes currently <- this could change

			ShapedRecipe templateRec = IRecipeSerializer.SHAPED_RECIPE.fromJson(recipeId, json);
			return new DetailedCrafterRec(recipeId, templateRec.getGroup(), path, true, templateRec.getRecipeWidth(), templateRec.getRecipeHeight(), templateRec.getIngredients(), templateRec.getResultItem());
		}

		@Override
		public DetailedCrafterRec fromNetwork(ResourceLocation recipeId, PacketBuffer buffer){
			if(!buffer.readBoolean()){
				return new DetailedCrafterRec(recipeId, "", EnumPath.ALCHEMY, false, 0, 0, NonNullList.create(), ItemStack.EMPTY);
			}
			EnumPath path = EnumPath.fromIndex(buffer.readByte());
			ShapedRecipe templateRec = IRecipeSerializer.SHAPED_RECIPE.fromNetwork(recipeId, buffer);
			return new DetailedCrafterRec(recipeId, templateRec.getGroup(), path, true, templateRec.getRecipeWidth(), templateRec.getRecipeHeight(), templateRec.getIngredients(), templateRec.getResultItem());
		}

		@Override
		public void toNetwork(PacketBuffer buffer, DetailedCrafterRec recipe){
			buffer.writeBoolean(recipe.active);
			buffer.writeByte(recipe.path.getIndex());
			IRecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe);
		}
	}
}

package com.Da_Technomancer.crossroads.items.crafting.recipes;

import com.Da_Technomancer.crossroads.API.EnumPath;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class DetailedCrafterRec extends ShapedRecipe{

	private final EnumPath path;

	public DetailedCrafterRec(ResourceLocation idIn, String groupIn, EnumPath path, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn){
		super(idIn, groupIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn);
		this.path = path;
	}

	public EnumPath getPath(){
		return path;
	}

	@Override
	public ItemStack getIcon(){
		return new ItemStack(CRBlocks.detailedCrafter);
	}

	@Override
	public IRecipeSerializer<?> getSerializer(){
		return RecipeHolder.DETAILED_SERIAL;
	}

	@Override
	public IRecipeType<?> getType(){
		return RecipeHolder.DETAILED_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>  implements IRecipeSerializer<DetailedCrafterRec> {

		@Override
		public DetailedCrafterRec read(ResourceLocation recipeId, JsonObject json){
			EnumPath path = EnumPath.fromName(JSONUtils.getString(json, "path"));
			if(path == null){
				throw new JsonParseException("Invalid path/no path set");
			}

			//Currently, the method of specifying a Detailed Crafter recipe is the same as for a vanilla recipe,
			//Except type is crossroads:detailed_crafter
			//Path is specified as the name of the path with the key "path"
			//And only shaped recipes are supported. No shapeless recipes currently <- this could change

			ShapedRecipe templateRec = IRecipeSerializer.CRAFTING_SHAPED.read(recipeId, json);
			return new DetailedCrafterRec(recipeId, templateRec.getGroup(), path, templateRec.getRecipeWidth(), templateRec.getRecipeHeight(), templateRec.getIngredients(), templateRec.getRecipeOutput());
		}

		@Override
		public DetailedCrafterRec read(ResourceLocation recipeId, PacketBuffer buffer){
			EnumPath path = EnumPath.fromIndex(buffer.readByte());
			ShapedRecipe templateRec = IRecipeSerializer.CRAFTING_SHAPED.read(recipeId, buffer);
			return new DetailedCrafterRec(recipeId, templateRec.getGroup(), path, templateRec.getRecipeWidth(), templateRec.getRecipeHeight(), templateRec.getIngredients(), templateRec.getRecipeOutput());
		}

		@Override
		public void write(PacketBuffer buffer, DetailedCrafterRec recipe){
			buffer.writeByte(recipe.path.getIndex());
			IRecipeSerializer.CRAFTING_SHAPED.write(buffer, recipe);
		}
	}
}

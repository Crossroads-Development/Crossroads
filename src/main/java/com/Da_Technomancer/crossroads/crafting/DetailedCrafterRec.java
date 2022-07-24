package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.EnumPath;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

public class DetailedCrafterRec extends ShapedRecipe implements IOptionalRecipe<CraftingContainer>{

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
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.DETAILED_SERIAL;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.DETAILED_TYPE;
	}

	@Override
	public boolean matches(CraftingContainer inv, Level world){
		return active && super.matches(inv, world);
	}

	public static class Serializer  implements RecipeSerializer<DetailedCrafterRec> {

		@Override
		public DetailedCrafterRec fromJson(ResourceLocation recipeId, JsonObject json){
			if(!CraftingUtil.isActiveJSON(json)){
				return new DetailedCrafterRec(recipeId, "", EnumPath.ALCHEMY, false, 0, 0, NonNullList.create(), ItemStack.EMPTY);
			}

			EnumPath path = EnumPath.fromName(GsonHelper.getAsString(json, "path"));
			if(path == null){
				throw new JsonParseException("Invalid path/no path set");
			}

			//Currently, the method of specifying a Detailed Crafter recipe is the same as for a vanilla recipe,
			//Except type is crossroads:detailed_crafter
			//Path is specified as the name of the path with the key "path"
			//And only shaped recipes are supported. No shapeless recipes currently <- this could change

			ShapedRecipe templateRec = RecipeSerializer.SHAPED_RECIPE.fromJson(recipeId, json);
			return new DetailedCrafterRec(recipeId, templateRec.getGroup(), path, true, templateRec.getRecipeWidth(), templateRec.getRecipeHeight(), templateRec.getIngredients(), templateRec.getResultItem());
		}

		@Override
		public DetailedCrafterRec fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
			if(!buffer.readBoolean()){
				return new DetailedCrafterRec(recipeId, "", EnumPath.ALCHEMY, false, 0, 0, NonNullList.create(), ItemStack.EMPTY);
			}
			EnumPath path = EnumPath.fromIndex(buffer.readByte());
			ShapedRecipe templateRec = RecipeSerializer.SHAPED_RECIPE.fromNetwork(recipeId, buffer);
			return new DetailedCrafterRec(recipeId, templateRec.getGroup(), path, true, templateRec.getRecipeWidth(), templateRec.getRecipeHeight(), templateRec.getIngredients(), templateRec.getResultItem());
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, DetailedCrafterRec recipe){
			buffer.writeBoolean(recipe.active);
			if(recipe.active){
				buffer.writeByte(recipe.path.getIndex());
				RecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe);
			}
		}
	}
}

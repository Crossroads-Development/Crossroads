package com.Da_Technomancer.crossroads.items.crafting.recipes;

import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class IceboxRec implements IRecipe<IInventory>{

	private final ResourceLocation id;
	private final String group;
	private final Ingredient ingr;
	private final float cooling;

	public IceboxRec(ResourceLocation location, String name, Ingredient input, double cooling){
		id = location;
		group = name;
		ingr = input;
		this.cooling = (float) cooling;
	}

	public float getCooling(){
		return cooling;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn){
		return ingr.test(inv.getStackInSlot(0));
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
		return new ItemStack(CrossroadsBlocks.icebox);
	}

	@Override
	public ResourceLocation getId(){
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer(){
		return RecipeHolder.COOLING_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public IRecipeType<?> getType(){
		return RecipeHolder.COOLING_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<IceboxRec>{

		@Override
		public IceboxRec read(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = JSONUtils.getString(json, "group", "");
			Ingredient ingredient;
			if(JSONUtils.isJsonArray(json, "ingredient")){
				ingredient = Ingredient.deserialize(JSONUtils.getJsonArray(json, "ingredient"));
			}else{
				ingredient = Ingredient.deserialize(JSONUtils.getJsonObject(json, "ingredient"));
			}

			//Output specified as 1 float tag
			double cooling = JSONUtils.getFloat(json, "cooling");
			return new IceboxRec(recipeId, s, ingredient, cooling);
		}

		@Nullable
		@Override
		public IceboxRec read(ResourceLocation recipeId, PacketBuffer buffer){
			String s = buffer.readString(Short.MAX_VALUE);
			Ingredient ingredient = Ingredient.read(buffer);
			float cooling = buffer.readFloat();
			return new IceboxRec(recipeId, s, ingredient, cooling);
		}

		@Override
		public void write(PacketBuffer buffer, IceboxRec recipe){
			buffer.writeString(recipe.getGroup());
			recipe.getIngredients().get(0).write(buffer);
			buffer.writeFloat(recipe.cooling);
		}
	}
}

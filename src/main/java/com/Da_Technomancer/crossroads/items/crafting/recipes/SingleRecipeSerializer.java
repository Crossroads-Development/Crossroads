package com.Da_Technomancer.crossroads.items.crafting.recipes;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SingleItemRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * The vanilla version of this interface is package locked. Presumably, Forge will eventually patch this. Until then, we copy it (with tiny modificiations)
 * @param <T> The SingleItemRecipe type this (de)serializes
 */
public class SingleRecipeSerializer<T extends SingleItemRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T>{

	private final IRecipeFactory<T> factory;

	protected SingleRecipeSerializer(IRecipeFactory<T> factory){
		this.factory = factory;
	}

	@Override
	public T read(ResourceLocation recipeId, JsonObject json){
		String s = JSONUtils.getString(json, "group", "");
		Ingredient ingredient;
		if(JSONUtils.isJsonArray(json, "ingredient")){
			ingredient = Ingredient.deserialize(JSONUtils.getJsonArray(json, "ingredient"));
		}else{
			ingredient = Ingredient.deserialize(JSONUtils.getJsonObject(json, "ingredient"));
		}

		String s1 = JSONUtils.getString(json, "result");
		int i = JSONUtils.getInt(json, "count");
		ItemStack itemstack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(s1)), i);
		return factory.create(recipeId, s, ingredient, itemstack);
	}

	@Override
	public T read(ResourceLocation recipeId, PacketBuffer buffer){
		String s = buffer.readString(32767);
		Ingredient ingredient = Ingredient.read(buffer);
		ItemStack itemstack = buffer.readItemStack();
		return factory.create(recipeId, s, ingredient, itemstack);
	}

	@Override
	public void write(PacketBuffer buffer, T recipe){
		buffer.writeString(recipe.getGroup());
		recipe.getIngredients().get(0).write(buffer);
		buffer.writeItemStack(recipe.getRecipeOutput());
	}

	public interface IRecipeFactory<T extends SingleItemRecipe>{
		T create(ResourceLocation location, String name, Ingredient input, ItemStack output);
	}
}

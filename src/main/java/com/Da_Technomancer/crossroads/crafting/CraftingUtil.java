package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.crafting.recipes.BlockIngredient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class CraftingUtil{

	/**
	 * Reads a FluidStack from JSON. Throws an exception if it fails
	 *
	 * Format:
	 *
	 * <memberName>:
	 * {
	 *     "fluid": <fluid name; ex: water or crossroads:distilled_water>
	 *     "amount": <size>
	 * }
	 *
	 * Does not currently support NBT tags
	 *
	 * @param json The object to read from
	 * @param memberName The name of the tag
	 * @return The defined fluidstack
	 */
	public static FluidStack getFluidStack(JsonObject json, String memberName){
		JsonObject obj = JSONUtils.getJsonObject(json, memberName);
		String name = JSONUtils.getString(obj, "fluid");
		Fluid f = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(name));
		int qty = JSONUtils.getInt(obj, "amount");
		//Note: Does not currently support NBT
		return f == null || qty <= 0 ? FluidStack.EMPTY : new FluidStack(f, qty);
	}

	/**
	 *
	 * Reads a FluidStack from JSON. Returns fallback if it fails
	 *
	 * Format:
	 *
	 * "<memberName>":
	 * {
	 *     "fluid": "<fluid name; ex: water or crossroads:distilled_water>"
	 *     "amount": <size>
	 * }
	 *
	 * Does not currently support NBT tags
	 *
	 * @param json The object to read from
	 * @param memberName The name of the tag
	 * @param fallback The value to return if no such fluidstack is defined
	 * @return The defined fluidstack
	 */
	public static FluidStack getFluidStack(JsonObject json, String memberName, FluidStack fallback){
		FluidStack out;
		try{
			out = getFluidStack(json, memberName);
		}catch(JsonParseException | IllegalArgumentException e){
			out = fallback;
		}
		return out;
	}

	public static ItemStack getItemStack(JsonObject json, String memberName, boolean allowDirect, boolean nbt){
		if(allowDirect && json.has("item")){
			return CraftingHelper.getItemStack(json, nbt);
		}
		if(memberName.isEmpty()){
			throw new JsonSyntaxException("No item defined");
		}
		return CraftingHelper.getItemStack(json.getAsJsonObject(memberName), nbt);
	}

	public static ItemStack getItemStack(JsonObject json, String memberName, boolean allowDirect, boolean nbt, ItemStack fallback){
		ItemStack out;
		try{
			out = getItemStack(json, memberName, allowDirect, nbt);
		}catch(JsonParseException | IllegalArgumentException e){
			out = fallback;
		}
		return out;
	}

	public static Ingredient getIngredient(JsonElement json, String memberName, boolean allowDirect){
		if(json.isJsonObject()){
			JsonObject jsonO = (JsonObject) json;
			if(jsonO.has(memberName)){
				return Ingredient.deserialize(((JsonObject) json).get(memberName));
			}
		}
		if(allowDirect){
			return Ingredient.deserialize(json);
		}
		throw new JsonParseException("Non-Ingredient passed as JSON ingredient");
	}

	public static BlockIngredient getBlockIngredient(JsonElement json, String memberName, boolean allowDirect){
		if(json.isJsonObject()){
			JsonObject jsonO = (JsonObject) json;
			if(jsonO.has(memberName)){
				return BlockIngredient.readFromJSON(((JsonObject) json).get(memberName));
			}
		}
		if(allowDirect){
			return BlockIngredient.readFromJSON(json);
		}
		throw new JsonParseException("Non-BlockIngredient passed as JSON ingredient");
	}

	public static boolean isActiveJSON(JsonObject json){
		return JSONUtils.getBoolean(json, "active", true);
	}
}

package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.crafting.recipes.BlockIngredient;
import com.Da_Technomancer.crossroads.crafting.recipes.FluidIngredient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.awt.*;

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
		JsonObject obj = GsonHelper.getAsJsonObject(json, memberName);
		String name = GsonHelper.getAsString(obj, "fluid");
		Fluid f = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(name));
		int qty = GsonHelper.getAsInt(obj, "amount");
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
				return Ingredient.fromJson(((JsonObject) json).get(memberName));
			}
		}
		if(allowDirect){
			return Ingredient.fromJson(json);
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

	/**
	 * Parses a fluid ingredient
	 * @param json The JSON to read from. Could be a JsonObject or JsonArray
	 * @param memberName The name of the element in json containing the fluid ingredient definition
	 * @param allowDirect If true, attempt to parse json itself as the fluid ingredient if no element with the member name is found
	 * @return The fluid ingredient specified in json
	 */
	public static FluidIngredient getFluidIngredient(JsonElement json, String memberName, boolean allowDirect){
		if(json.isJsonObject()){
			JsonObject jsonO = (JsonObject) json;
			if(jsonO.has(memberName)){
				return FluidIngredient.readFromJSON(((JsonObject) json).get(memberName));
			}
		}
		if(allowDirect){
			return FluidIngredient.readFromJSON(json);
		}
		throw new JsonParseException("Non-FluidIngredient passed as JSON ingredient");
	}

	/**
	 * Parses a fluid ingredient and gets a quantity associated with it
	 * Expects 'fluid_amount' to be specified in json directly
	 * @param json The JSON to read from. Could be a JsonObject or JsonArray
	 * @param memberName The name of the element in json containing the fluid ingredient definition
	 * @param allowDirect If true, attempt to parse json itself as the fluid ingredient if no element with the member name is found
	 * @param defaultQuantity The default quantity to return if none was specified. 0 or negative values will require a value to be specified (no default)
	 * @return The fluid ingredient and quantity specified in json
	 */
	public static Pair<FluidIngredient, Integer> getFluidIngredientAndQuantity(JsonElement json, String memberName, boolean allowDirect, int defaultQuantity){
		FluidIngredient ingr = getFluidIngredient(json, memberName, allowDirect);
		int quantity = defaultQuantity;
		if(json.isJsonObject()){
			quantity = GsonHelper.getAsInt((JsonObject) json, "fluid_amount", defaultQuantity);
		}
		if(quantity <= 0){
			throw new JsonParseException("No/invalid quantity specified for fluid ingredient");
		}

		return Pair.of(ingr, quantity);
	}

	public static boolean isActiveJSON(JsonObject json){
		return GsonHelper.getAsBoolean(json, "active", true);
	}

	public static Color getColor(JsonObject json, String memberName, @Nullable Color fallback){
		if(!json.has(memberName)){
			return fallback;
		}
		String colorCode = GsonHelper.getAsString(json, memberName);
		if(colorCode.length() != 6 && colorCode.length() != 8){
			return fallback;
		}
		try{
			return new Color(Long.valueOf(colorCode, 16).intValue(), colorCode.length() == 8);
		}catch(NumberFormatException e){
			return fallback;
		}
	}
}

package com.Da_Technomancer.crossroads.api.crafting;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.essentials.Essentials;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryManager;
import net.minecraftforge.registries.tags.ITagManager;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;

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

	/**
	 * Returns an entry from the Tag
	 * If the Tag is set to preserve order, it will reliably return the first entry.
	 * Otherwise, any entry could be returned- but which entry will remain consistent between calls.
	 * If the tag is unordered, this method will prioritize CR items, then essentials items, then vanilla items, then all other items, prioritized by alphabetical order of the registry name
	 * @param tag The Tag to return an entry from
	 * @param <T> The type of the tag. Normally Block or Item
	 * @return An entry in the tag, or null if the tag is empty.
	 */
	@Nullable
	public static <T> T getTagEntry(TagKey<T> tag){
		ITagManager<T> manager = getTagManagerForKey(tag);
		Comparator<T> comparator = RegNameComparator.getComparator(tag.registry());
		return manager.getTag(tag).stream().min(comparator).orElse(null);
	}

	public static <T> T getPreferredEntry(Collection<T> entries, ResourceKey<? extends Registry<T>> registry){
		//We can use the registry name to prioritize the result. Applies to items and blocks (among others)
		Comparator<T> comparator = RegNameComparator.getComparator(registry);
		return entries.stream().min(comparator).orElse(null);
	}

	public static <T> TagKey<T> getTagKey(ResourceKey<? extends Registry<T>> registry, ResourceLocation tagLocation){
		return TagKey.create(registry, tagLocation);
	}

	@SuppressWarnings("unchecked")
	public static <T> ITagManager<T> getTagManagerForKey(TagKey<T> tagKey){
		return (ITagManager<T>) RegistryManager.ACTIVE.getRegistry(tagKey.registry().location()).tags();
	}

	public static <T> boolean tagContains(TagKey<T> tagKey, T thing){
		return getTagManagerForKey(tagKey).getTag(tagKey).contains(thing);
	}

	/**
	 * A comparator that sorts by registry name, prioritizing entries in order:
	 * From Crossroads
	 * From Essentials
	 * From vanilla Minecraft
	 * all others.
	 * Sorting order defaults to alphabetical.
	 */
	private static class RegNameComparator<T> implements Comparator<T>{

		@SuppressWarnings("unchecked")
		public static <A> RegNameComparator<A> getComparator(ResourceKey<? extends Registry<A>> registry){
			if(comparators.containsKey(registry)){
				return (RegNameComparator<A>) comparators.get(registry);
			}
			RegNameComparator<A> result = new RegNameComparator<>(registry);
			comparators.put(registry, result);
			return result;
		}

		private static final HashMap<ResourceKey<? extends Registry<?>>, RegNameComparator<?>> comparators = new HashMap<>(1);

		private final ResourceKey<? extends Registry<T>> registry;

		private RegNameComparator(ResourceKey<? extends Registry<T>> registry){
			this.registry = registry;
		}

		@Override
		public int compare(T a, T b){
			if(a.equals(b)){
				return 0;
			}
			ResourceLocation aLocation = MiscUtil.getRegistryName(a, registry);
			ResourceLocation bLocation = MiscUtil.getRegistryName(b, registry);
			//assert aLocation != null && bLocation != null;
			String aNamespace = aLocation.getNamespace();
			String bNamespace = bLocation.getNamespace();
			int aNamespaceWeight = switch(aNamespace){
				case Crossroads.MODID -> 3;
				case Essentials.MODID -> 2;
				case "minecraft" -> 1;
				default -> 0;
			};
			int bNamespaceWeight = switch(bNamespace){
				case Crossroads.MODID -> 3;
				case Essentials.MODID -> 2;
				case "minecraft" -> 1;
				default -> 0;
			};
			if(aNamespaceWeight != bNamespaceWeight){
				return bNamespaceWeight - aNamespaceWeight;//Crossroads < Essentials < vanilla < anything else
			}
			return aLocation.compareTo(bLocation);//Default to alphabetical of the entire resource location
		}
	}
}

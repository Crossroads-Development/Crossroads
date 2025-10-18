package com.Da_Technomancer.crossroads.api.crafting;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.essentials.Essentials;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

public class CraftingUtil{

	public static final Codec<Color> COLOR_CODEC = Codec.STRING.comapFlatMap(str -> {
		str = str.toLowerCase();
		//Trim off any leading # or 0x
		if(!str.isEmpty() && str.charAt(0) == '#'){
			str = str.substring(1);
		}else if(str.startsWith("0x")){
			str = str.substring(2);
		}
		if(str.length() != 6 && str.length() != 8){
			return DataResult.error(() -> "Must be a 6 or 8 character hexidecimal color");
		}
		try{
			return DataResult.success(new Color(Long.valueOf(str, 16).intValue(), str.length() == 8));
		}catch(NumberFormatException e){
			return DataResult.error(() -> "Must be a 6 or 8 character hexidecimal color");
		}}, col -> {
		String str = Integer.toHexString(col.getRGB());
		//Not that this encoder ever gets used, but need to enforce 6 or 8 character rule
		String str2 = str;
		for(int i = 0; i < 8 - str.length(); i++){
			str2 = "0" + str2;
		}
		return str2;
	});
	public static final StreamCodec<ByteBuf, Color> COLOR_STREAM_CODEC = ByteBufCodecs.INT.map(colInt -> new Color(colInt, true), Color::getRGB);

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
	 * Allows specifying either "fieldName": (T) or "fieldName": [(T), (T), ....]
	 * @param baseCodec Codec for (T)
	 * @return Codec mapping to a list of (T) with two allowed formats
	 * @param <T> Base type
	 */
	public static <T> Codec<List<T>> singleOrListCodec(Codec<T> baseCodec){
		return singleOrListCodec(baseCodec, 0, Integer.MAX_VALUE);
	}

	/**
	 * Allows specifying either "fieldName": (T) or "fieldName": [(T), (T), ....]
	 * @param baseCodec Codec for (T)
	 * @param min Minimum list size
	 * @param max Maximum list size
	 * @return Codec mapping to a list of (T) with two allowed formats
	 * @param <T> Base type
	 */
	public static <T> Codec<List<T>> singleOrListCodec(Codec<T> baseCodec, int min, int max){
		assert min < 2;
		return Codec.withAlternative(baseCodec.listOf(min, max), baseCodec.xmap(List::of, List::getFirst));
	}

	public static MapCodec<String> recipeGroupFieldCodec(){
		return Codec.STRING.optionalFieldOf("name", "");
	}

	/**
	 * This is for when you have a Codec to some composite data type (ex. ItemStack.CODEC) and you want two options for how it can be specified in JSON:
	 *  - Nested style (normal ItemStack.CODEC.fieldOf(fieldName)) where it's {"fieldName": {...itemstack fields}}
	 *  - Direct style where it's {...itemstack fields} without an enclosing "fieldName" object
	 *  This will decode from either style, but encodes only to nested style
	 * Example usage: alternativeEncodeDirect(ItemStack.CODEC, "output").forGetter(myGetter) as part of a RecordCodecBuilder call
	 * This only works for some kinds of elementCodecs, mainly the ones created by RecordCodecBuilder or a Codec.lazyInitialized RecordCodecBuilder
	 * @param elementCodec Base codec for a composite data type
	 * @param fieldName Field name (optional in the JSON); if empty string, only direct encode will work
	 * @return A MapCodec with two alternative decoding styles for the Codec
	 * @param <T> The data type being encoded by the codecs
	 */
	public static <T> MapCodec<T> allowDirectEncode(Codec<T> elementCodec, String fieldName){
		MapCodec<T> directMapCodec = MapCodec.assumeMapUnsafe(elementCodec);//Only works for some types of elementCodec
		if(fieldName.isEmpty()){
			return directMapCodec;
		}
		return NeoForgeExtraCodecs.withAlternative(elementCodec.fieldOf(fieldName), directMapCodec);
	}

	/**
	 * Makes this an optional field
	 *
	 * This is for when you have a Codec to some composite data type (ex. ItemStack.CODEC) and you want two options for how it can be specified in JSON:
	 *  - Nested style (normal ItemStack.CODEC.fieldOf(fieldName)) where it's {"fieldName": {...itemstack fields}}
	 *  - Direct style where it's {...itemstack fields} without an enclosing "fieldName" object
	 *  This will decode from either style, but encodes only to nested style
	 * Example usage: alternativeEncodeDirect(ItemStack.CODEC, "output").forGetter(myGetter) as part of a RecordCodecBuilder call
	 * This only works for some kinds of elementCodecs, mainly the ones created by RecordCodecBuilder or a Codec.lazyInitialized RecordCodecBuilder
	 * @param elementCodec Base codec for a composite data type
	 * @param fieldName Field name (optional in the JSON); if empty string, only direct encode will work
	 * @param fallback Fallback value if not present in JSON
	 * @return A MapCodec with two alternative decoding styles for the Codec
	 * @param <T> The data type being encoded by the codecs
	 */
	public static <T> MapCodec<T> allowDirectEncode(Codec<T> elementCodec, String fieldName, T fallback){
		MapCodec<T> directMapCodec = MapCodec.assumeMapUnsafe(elementCodec).orElse(fallback);//Only works for some types of elementCodec
		if(fieldName.isEmpty()){
			return directMapCodec;
		}
		return NeoForgeExtraCodecs.withAlternative(elementCodec.fieldOf(fieldName), directMapCodec);
	}

	public static MapCodec<ItemStack> itemStackMapCodec(String fieldName, boolean allowDirect){
		if(allowDirect){
			return allowDirectEncode(ItemStack.OPTIONAL_CODEC, fieldName);
		}
		assert !fieldName.isEmpty();
		return ItemStack.OPTIONAL_CODEC.fieldOf(fieldName);
	}

	public static MapCodec<ItemStack> itemStackMapCodec(String fieldName, boolean allowDirect, ItemStack fallback){
		if(allowDirect){
			return allowDirectEncode(ItemStack.OPTIONAL_CODEC, fieldName, fallback);
		}
		assert !fieldName.isEmpty();
		return ItemStack.OPTIONAL_CODEC.optionalFieldOf(fieldName, fallback);
	}

	public static MapCodec<FluidStack> fluidStackMapCodec(String fieldName, boolean allowDirect){
		if(allowDirect){
			return allowDirectEncode(FluidStack.OPTIONAL_CODEC, fieldName);
		}
		assert !fieldName.isEmpty();
		return FluidStack.OPTIONAL_CODEC.fieldOf(fieldName);
	}

	public static MapCodec<FluidStack> fluidStackMapCodec(String fieldName, boolean allowDirect, FluidStack fallback){
		if(allowDirect){
			return allowDirectEncode(FluidStack.OPTIONAL_CODEC, fieldName, fallback);
		}
		assert !fieldName.isEmpty();
		return FluidStack.OPTIONAL_CODEC.optionalFieldOf(fieldName, fallback);
	}

	public static MapCodec<Ingredient> itemIngredientMapCodec(String fieldName, boolean allowDirect){
		if(allowDirect){
			return allowDirectEncode(Ingredient.CODEC, fieldName);
		}
		assert !fieldName.isEmpty();
		return Ingredient.CODEC.fieldOf(fieldName);
	}

	public static MapCodec<FluidIngredient> fluidIngredientMapCodec(String fieldName, boolean allowDirect){
		if(allowDirect){
			return allowDirectEncode(FluidIngredient.CODEC, fieldName);
		}
		assert !fieldName.isEmpty();
		return FluidIngredient.CODEC.fieldOf(fieldName);
	}

	public static MapCodec<BlockIngredient> blockIngredientMapCodec(String fieldName, boolean allowDirect){
		if(allowDirect){
			return allowDirectEncode(BlockIngredient.CODEC, fieldName);
		}
		assert !fieldName.isEmpty();
		return BlockIngredient.CODEC.fieldOf(fieldName);
	}

	/**
	 * Returns an entry from the Tag
	 * If there are multiple entries in the tag, this method will prioritize CR things, then essentials things, then vanilla things, then all other items, prioritized by alphabetical order of the registry name
	 * @param tag The Tag to return an entry from
	 * @param <T> The type of the tag. Normally Block or Item
	 * @return An entry in the tag, or null if the tag is empty.
	 */
	@Nullable
	public static <T> T getTagEntry(TagKey<T> tag){
		HashSet<T> contents = getTagContents(tag);
		Comparator<T> comparator = RegNameComparator.getComparator(tag.registry());
		return contents.stream().min(comparator).orElse(null);
	}

	public static <T> HashSet<T> getTagContents(TagKey<T> tag){
		HashSet<T> entries = new HashSet<>();
		getTagContents(tag, getRegistryForKey(tag), entries);
		return entries;
	}

	private static <T> void getTagContents(TagKey<T> tag, Registry<T> manager, HashSet<T> entries){
		manager.getTag(tag).ifPresent(named -> named.unwrap().ifLeft(innerTagKey -> getTagContents(innerTagKey, manager, entries)).ifRight(holderList -> holderList.stream().map(Holder::value).forEach(entries::add)));
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
	public static <T> Registry<T> getRegistryForKey(TagKey<T> tagKey){
		return (Registry<T>) BuiltInRegistries.REGISTRY.get(tagKey.registry().location());
	}

	public static <T> boolean tagContains(TagKey<T> tagKey, T thing){
		Optional<HolderSet.Named<T>> tagHolderSet;
		if((tagHolderSet = getRegistryForKey(tagKey).getTag(tagKey)).isPresent()){
			//TODO don't actually know what Holder.direct() is doing but it seems like something vaguely appropriate
			// to inline here.
			return tagHolderSet.get().contains(Holder.direct(thing));
		}else{
			throw new IllegalArgumentException(
					"tagContains() called using generic type that does not possess an associated registry"
			);
		}
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

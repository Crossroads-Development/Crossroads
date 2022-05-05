package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.Essentials;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;
import net.minecraftforge.registries.tags.ITagManager;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Comparator;

public class CRItemTags{

	private static final String FORGE = "forge";
	private static final String CR = Crossroads.MODID;


	public static final TagKey<Item> INGOTS_COPPER = getTagKey(ForgeRegistries.Keys.ITEMS, new ResourceLocation(FORGE, "ingots/copper"));

	public static final TagKey<Item> GEMS_PURE_QUARTZ = getTagKey(ForgeRegistries.Keys.ITEMS, new ResourceLocation(CR, "gems/pure_quartz"));
	public static final TagKey<Item> GEMS_RUBY = getTagKey(ForgeRegistries.Keys.ITEMS, new ResourceLocation(FORGE, "gems/ruby"));
	public static final TagKey<Item> GEMS_VOID = getTagKey(ForgeRegistries.Keys.ITEMS, new ResourceLocation(CR, "gems/void"));

	public static final TagKey<Item> EXPLODE_IF_KNOCKED = getTagKey(ForgeRegistries.Keys.ITEMS, new ResourceLocation(CR, "explode_on_hit"));
	public static final TagKey<Item> SALT_REACTOR_COOLANT = getTagKey(ForgeRegistries.Keys.ITEMS, new ResourceLocation(CR, "salt_reactor_coolant"));

	public static final TagKey<Item> INCUBATOR_EGG = getTagKey(ForgeRegistries.Keys.ITEMS, new ResourceLocation(CR, "incubator_egg"));

	/**
	 * A comparator that sorts by registry name, prioritizing entries in order:
	 * From Crossroads
	 * From Essentials
	 * From vanilla Minecraft
	 * all others.
	 * Sorting order defaults to alphabetical.
	 */
	private static final Comparator<? extends IForgeRegistryEntry<?>> compareByRegName = (a, b) -> {
		if(a.equals(b)){
			return 0;
		}
		ResourceLocation aLocation = a.getRegistryName();
		ResourceLocation bLocation = b.getRegistryName();
//		assert aLocation != null && bLocation != null;
		String aNamespace = aLocation.getNamespace();
		String bNamespace = bLocation.getNamespace();
		int aNamespaceWeight = switch(aNamespace){
			case CR -> 3;
			case Essentials.MODID -> 2;
			case "minecraft" -> 1;
			default -> 0;
		};
		int bNamespaceWeight = switch(bNamespace){
			case CR -> 3;
			case Essentials.MODID -> 2;
			case "minecraft" -> 1;
			default -> 0;
		};
		if(aNamespaceWeight != bNamespaceWeight){
			return bNamespaceWeight - aNamespaceWeight;//Crossroads < Essentials < vanilla < anything else
		}
		return aLocation.compareTo(bLocation);//Default to alphabetical of the entire resource location
	};

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
	public static <T extends IForgeRegistryEntry<T>> T getTagEntry(TagKey<T> tag){
		ITagManager<T> manager = getTagManagerForKey(tag);
		return manager.getTag(tag).stream().min((Comparator<? super T>) compareByRegName).orElse(null);
	}

	public static <T extends IForgeRegistryEntry<T>> T getPreferredEntry(Collection<T> entries){
		//We can use the registry name to prioritize the result. Applies to items and blocks (among others)
		return entries.stream().min((Comparator<? super T>) compareByRegName).orElse(null);
	}

	public static <T extends IForgeRegistryEntry<T>> TagKey<T> getTagKey(ResourceKey<? extends Registry<T>> registry, ResourceLocation tagLocation){
		return TagKey.create(registry, tagLocation);
	}

	@SuppressWarnings("unchecked")
	public static <T extends IForgeRegistryEntry<T>> ITagManager<T> getTagManagerForKey(TagKey<T> tagKey){
		return (ITagManager<T>) RegistryManager.ACTIVE.getRegistry(tagKey.registry().location()).tags();
	}

	public static <T extends IForgeRegistryEntry<T>> boolean tagContains(TagKey<T> tagKey, T thing){
		return getTagManagerForKey(tagKey).getTag(tagKey).contains(thing);
	}
}

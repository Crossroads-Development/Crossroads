package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.Essentials;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;

public class CRItemTags{

	private static final String FORGE = "forge";
	private static final String CR = Crossroads.MODID;

	public static final ITag<Item> SALT = ItemTags.makeWrapperTag(FORGE + ":dusts/salt");
	public static final ITag<Item> SLAG = ItemTags.makeWrapperTag(FORGE + ":dusts/slag");
	public static final ITag<Item> SULFUR = ItemTags.makeWrapperTag(FORGE + ":dusts/sulfur");
	public static final ITag<Item> ALC_SALT = ItemTags.makeWrapperTag(CR + ":dusts/alchemy_salt");
	public static final ITag<Item> VANADIUM = ItemTags.makeWrapperTag(CR + ":dusts/vanadium");
	public static final ITag<Item> MERCURY = ItemTags.makeWrapperTag(FORGE + ":gems/mercury");
	public static final ITag<Item> DENSUS = ItemTags.makeWrapperTag(CR + ":gems/densus");
	public static final ITag<Item> ANTI_DENSUS = ItemTags.makeWrapperTag(CR + ":gems/anti_densus");
	public static final ITag<Item> CAVORITE = ItemTags.makeWrapperTag(CR + ":gems/cavorite");
	public static final ITag<Item> ALCH_CRYSTAL = ItemTags.makeWrapperTag(CR + ":gems/alch_cryst");
	public static final ITag<Item> DUSTS_ADAMANT = ItemTags.makeWrapperTag(CR + ":dusts/adamant");
	public static final ITag<Item> DUSTS_BEDROCK = ItemTags.makeWrapperTag(CR + ":dusts/bedrock");
	public static final ITag<Item> DUSTS_SULFURIC = ItemTags.makeWrapperTag(CR + ":dusts/sulfuric_acid");
	public static final ITag<Item> DUSTS_NITRIC = ItemTags.makeWrapperTag(CR + ":dusts/nitric_acid");
	public static final ITag<Item> DUSTS_HYDROCHLORIC = ItemTags.makeWrapperTag(CR + ":dusts/hydrochloric_acid");
	public static final ITag<Item> DUSTS_REGIA = ItemTags.makeWrapperTag(CR + ":dusts/aqua_regia");
	public static final ITag<Item> DUSTS_CHLORINE = ItemTags.makeWrapperTag(CR + ":dusts/chlorine");
	public static final ITag<Item> DUSTS_SULFUR_DIOXIDE = ItemTags.makeWrapperTag(CR + ":dusts/sulfur_dioxide");
	public static final ITag<Item> ALKAHEST = ItemTags.makeWrapperTag(CR + ":alkahest");
	public static final ITag<Item> ANTI_ALKAHEST = ItemTags.makeWrapperTag(CR + ":anti_alkahest");

	public static final ITag<Item> PURE_ICE = ItemTags.makeWrapperTag(CR + ":pure_ice");
	public static final ITag<Item> RAW_ICE = ItemTags.makeWrapperTag(CR + ":raw_ice");

	public static final ITag<Item> INGOTS_COPPER = ItemTags.makeWrapperTag(FORGE + ":ingots/copper");
	public static final ITag<Item> INGOTS_TIN = ItemTags.makeWrapperTag(FORGE + ":ingots/tin");
	public static final ITag<Item> NUGGETS_COPPER = ItemTags.makeWrapperTag(FORGE + ":nuggets/copper");
	public static final ITag<Item> NUGGETS_TIN = ItemTags.makeWrapperTag(FORGE + ":nuggets/tin");

	public static final ITag<Item> GEMS_RUBY = ItemTags.makeWrapperTag(FORGE + ":gems/ruby");

	public static final ITag<Item> EXPLODE_IF_KNOCKED = ItemTags.makeWrapperTag(CR + ":explode_on_hit");

	/**
	 * Returns an entry from the Tag
	 * If the Tag is set to preserve order, it will reliably return the first entry.
	 * Otherwise, any entry could be returned- but which entry will remain consistent between calls.
	 * If the tag in unordered, this method will prioritize CR items, then essentials items, then vanilla items, then all other items
	 * @param tag The Tag to return an entry from
	 * @param <T> The type of the tag. Normally Block or Item
	 * @return An entry in the tag.
	 */
	@Nullable
	public static <T extends IForgeRegistryEntry<T>> T getTagEntry(ITag<T> tag){
		Collection<T> elems = tag.getAllElements();
		if(elems.isEmpty()){
			return null;
		}
		T randEntry = elems.iterator().next();
		if(elems instanceof LinkedHashSet){
			return randEntry;//This is an ordered tag. Return the first entry
		}
		//We can use the registry name to prioritize the result. Applies to items and blocks (among others)
		Optional<T> opt = elems.stream().filter((t) -> t.getRegistryName().getNamespace().equals(CR)).findFirst();
		if(opt.isPresent()){
			return opt.get();
		}
		opt = elems.stream().filter((t) -> t.getRegistryName().getNamespace().equals(Essentials.MODID)).findFirst();
		if(opt.isPresent()){
			return opt.get();
		}
		opt = elems.stream().filter((t) -> t.getRegistryName().getNamespace().equals("minecraft")).findFirst();
		if(opt.isPresent()){
			return opt.get();
		}
		return randEntry;
	}
}

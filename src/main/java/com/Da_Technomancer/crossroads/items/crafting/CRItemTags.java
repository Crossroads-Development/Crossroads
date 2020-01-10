package com.Da_Technomancer.crossroads.items.crafting;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class CRItemTags{

	private static final String FORGE = "forge";
	private static final String CR = Crossroads.MODID;

	public static final Tag<Item> SALT = new ItemTags.Wrapper(new ResourceLocation(FORGE, "dusts/salt"));
	public static final Tag<Item> SLAG = new ItemTags.Wrapper(new ResourceLocation(FORGE, "dusts/slag"));
	public static final Tag<Item> SULFUR = new ItemTags.Wrapper(new ResourceLocation(FORGE, "dusts/sulfur"));
	public static final Tag<Item> ALC_SALT = new ItemTags.Wrapper(new ResourceLocation(CR, "dusts/alchemy_salt"));
	public static final Tag<Item> VANADIUM = new ItemTags.Wrapper(new ResourceLocation(CR, "dusts/vanadium"));
	public static final Tag<Item> MERCURY = new ItemTags.Wrapper(new ResourceLocation(FORGE, "gems/mercury"));
	public static final Tag<Item> DENSUS = new ItemTags.Wrapper(new ResourceLocation(CR, "gems/densus"));
	public static final Tag<Item> ANTI_DENSUS = new ItemTags.Wrapper(new ResourceLocation(CR, "gems/anti_densus"));
	public static final Tag<Item> CAVORITE = new ItemTags.Wrapper(new ResourceLocation(CR, "gems/cavorite"));
	public static final Tag<Item> ALCH_CRYSTAL = new ItemTags.Wrapper(new ResourceLocation(CR, "gems/alchemy_crystal"));
	public static final Tag<Item> DUSTS_ADAMANT = new ItemTags.Wrapper(new ResourceLocation(CR, "dusts/adamant"));
	public static final Tag<Item> DUSTS_BEDROCK = new ItemTags.Wrapper(new ResourceLocation(CR, "dusts/bedrock"));
	public static final Tag<Item> DUSTS_SULFURIC = new ItemTags.Wrapper(new ResourceLocation(CR, "dusts/sulfuric_acid"));
	public static final Tag<Item> DUSTS_NITRIC = new ItemTags.Wrapper(new ResourceLocation(CR, "dusts/nitric_acid"));
	public static final Tag<Item> DUSTS_HYDROCHLORIC = new ItemTags.Wrapper(new ResourceLocation(CR, "dusts/hydrochloric_acid"));
	public static final Tag<Item> DUSTS_REGIA = new ItemTags.Wrapper(new ResourceLocation(CR, "dusts/aqua_regia"));
	public static final Tag<Item> DUSTS_CHLORINE = new ItemTags.Wrapper(new ResourceLocation(CR, "dusts/chlorine"));
	public static final Tag<Item> DUSTS_SULFUR_DIOXIDE = new ItemTags.Wrapper(new ResourceLocation(CR, "dusts/sulfur_dioxide"));

	public static final Tag<Item> PURE_ICE = new ItemTags.Wrapper(new ResourceLocation(CR, "pure_ice"));
	public static final Tag<Item> RAW_ICE = new ItemTags.Wrapper(new ResourceLocation(CR, "raw_ice"));

	public static final Tag<Item> INGOTS_COPPER = new ItemTags.Wrapper(new ResourceLocation(FORGE, "ingots/copper"));
	public static final Tag<Item> INGOTS_TIN = new ItemTags.Wrapper(new ResourceLocation(FORGE, "ingots/tin"));
	public static final Tag<Item> NUGGETS_COPPER = new ItemTags.Wrapper(new ResourceLocation(FORGE, "nuggets/copper"));
	public static final Tag<Item> NUGGETS_TIN = new ItemTags.Wrapper(new ResourceLocation(FORGE, "nuggets/tin"));

	public static final Tag<Item> GEMS_RUBY = new ItemTags.Wrapper(new ResourceLocation(FORGE, "gems/ruby"));

	/**
	 * Returns an entry from the Tag
	 * @param tag The Tag to return an entry from
	 * @param <T> The type of the tag. Normally Block or Item
	 * @return An entry in the tag. If the Tag is set to preserve order, it will reliably return the first entry. Otherwise, any entry could be returned- but which entry will remain consistent between calls.
	 */
	public static <T> T getTagEntry(Tag<T> tag){
		return tag.getAllElements().iterator().next();
	}
}

package com.Da_Technomancer.crossroads.items.crafting;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class CRItemTags extends ItemTagsProvider{

	private static final String FORGE = "forge";
	private static final String CR = Crossroads.MODID;

	public static final Tag<Item> SULFUR = new ItemTags.Wrapper(new ResourceLocation(FORGE, "dusts/sulfur"));
	public static final Tag<Item> SALT = new ItemTags.Wrapper(new ResourceLocation(FORGE, "dusts/salt"));
	public static final Tag<Item> SLAG = new ItemTags.Wrapper(new ResourceLocation(FORGE, "slag"));
	public static final Tag<Item> ALC_SALT = new ItemTags.Wrapper(new ResourceLocation(CR, "dusts/alchemy_salt"));
	public static final Tag<Item> DENSUS = new ItemTags.Wrapper(new ResourceLocation(CR, "dusts/densus"));
	public static final Tag<Item> ANTI_DENSUS = new ItemTags.Wrapper(new ResourceLocation(CR, "dusts/anti_densus"));
	public static final Tag<Item> CAVORITE = new ItemTags.Wrapper(new ResourceLocation(CR, "gems/cavorite"));
	public static final Tag<Item> ALCH_CRYSTAL = new ItemTags.Wrapper(new ResourceLocation(CR, "gems/alchemy_crystal"));
	public static final Tag<Item> VANADIUM = new ItemTags.Wrapper(new ResourceLocation(CR, "dusts/vanadium"));
	public static final Tag<Item> MERCURY = new ItemTags.Wrapper(new ResourceLocation(FORGE, "mercury"));
	public static final Tag<Item> PURE_ICE = new ItemTags.Wrapper(new ResourceLocation(CR, "pure_ice"));
	public static final Tag<Item> RAW_ICE = new ItemTags.Wrapper(new ResourceLocation(CR, "raw_ice"));
	public static final Tag<Item> DUSTS_ADAMANT = new ItemTags.Wrapper(new ResourceLocation(CR, "dusts/adamant"));
	public static final Tag<Item> DUSTS_BEDROCK = new ItemTags.Wrapper(new ResourceLocation(CR, "dusts/bedrock"));
	public static final Tag<Item> DUSTS_SULFURIC = new ItemTags.Wrapper(new ResourceLocation(CR, "dusts/sulfuric_acid"));
	public static final Tag<Item> DUSTS_NITRIC = new ItemTags.Wrapper(new ResourceLocation(CR, "dusts/nitric_acid"));
	public static final Tag<Item> DUSTS_HYDROCHLORIC = new ItemTags.Wrapper(new ResourceLocation(CR, "dusts/hydrochloric_acid"));
	public static final Tag<Item> DUSTS_REGIA = new ItemTags.Wrapper(new ResourceLocation(CR, "dusts/aqua_regia"));
	public static final Tag<Item> DUSTS_CHLORINE = new ItemTags.Wrapper(new ResourceLocation(CR, "dusts/chlorine"));
	public static final Tag<Item> DUSTS_SULFUR_DIOXIDE = new ItemTags.Wrapper(new ResourceLocation(CR, "dusts/sulfur_dioxide"));

	public static final Tag<Item> INGOTS_COPPER = new ItemTags.Wrapper(new ResourceLocation(FORGE, "ingots/copper"));
	public static final Tag<Item> INGOTS_TIN = new ItemTags.Wrapper(new ResourceLocation(FORGE, "ingots/tin"));
	public static final Tag<Item> NUGGETS_COPPER = new ItemTags.Wrapper(new ResourceLocation(FORGE, "nuggets/copper"));
	public static final Tag<Item> NUGGETS_TIN = new ItemTags.Wrapper(new ResourceLocation(FORGE, "nuggets/tin"));

	public static final Tag<Item> GEMS_RUBY = new ItemTags.Wrapper(new ResourceLocation(FORGE, "gems/ruby"));

	public CRItemTags(DataGenerator generatorIn){
		super(generatorIn);
	}

	/**
	 * Returns an entry from the Tag
	 * @param tag The Tag to return an entry from
	 * @param <T> The type of the tag. Normally Block or Item
	 * @return An entry in the tag. If the Tag is set to preserve order, it will reliably return the first entry. Otherwise, any item could be returned.
	 */
	public static <T> T getTagEntry(Tag<T> tag){
		return tag.getAllElements().iterator().next();
	}

	@Override
	protected void registerTags(){
		//TODO this doesn't seem to work. May have to be done as JSON files

		//super.registerTags();
		//You might notice all of these are ordered
		//This is because ordered tags allow increased consistency, and I'm really not sure why they're unordered by default.
		getBuilder(SALT).ordered(true).add(CRItems.dustSalt);
		getBuilder(SULFUR).ordered(true).add(CRItems.sulfur);
		getBuilder(ALC_SALT).ordered(true).add(CRItems.wasteSalt);
		getBuilder(SLAG).ordered(true).add(CRItems.slag);
		getBuilder(ALCH_CRYSTAL).ordered(true).add(CRItems.alchCrystal);
		getBuilder(VANADIUM).ordered(true).add(CRItems.vanadiumOxide);
		getBuilder(MERCURY).ordered(true).add(CRItems.solidQuicksilver);
		getBuilder(PURE_ICE).ordered(true).add(Blocks.PACKED_ICE.asItem(), Blocks.BLUE_ICE.asItem());
		getBuilder(RAW_ICE).ordered(true).add(Blocks.ICE.asItem());
		getBuilder(DUSTS_ADAMANT).ordered(true).add(CRItems.adamant);
		getBuilder(DUSTS_BEDROCK).ordered(true).add(CRItems.bedrockDust);
		getBuilder(DUSTS_SULFURIC).ordered(true).add(CRItems.solidVitriol);
		getBuilder(DUSTS_NITRIC).ordered(true).add(CRItems.solidFortis);
		getBuilder(DUSTS_HYDROCHLORIC).ordered(true).add(CRItems.solidMuriatic);
		getBuilder(DUSTS_REGIA).ordered(true).add(CRItems.solidRegia);
		getBuilder(DUSTS_CHLORINE).ordered(true).add(CRItems.solidChlorine);
		getBuilder(DUSTS_SULFUR_DIOXIDE).ordered(true).add(CRItems.solidSO2);

		getBuilder(INGOTS_COPPER).ordered(true).add(OreSetup.ingotCopper);
		getBuilder(INGOTS_TIN).ordered(true).add(OreSetup.ingotTin);
		getBuilder(NUGGETS_COPPER).ordered(true).add(OreSetup.nuggetCopper);
		getBuilder(NUGGETS_TIN).ordered(true).add(OreSetup.nuggetTin);

		getBuilder(GEMS_RUBY).ordered(true).add(OreSetup.gemRuby);
	}

	@Override
	public String getName(){
		return Crossroads.MODNAME + " Item Tags";
	}
}

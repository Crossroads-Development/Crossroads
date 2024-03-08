package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class CRItemTags{

	private static final String FORGE = "forge";
	private static final String CR = Crossroads.MODID;


	public static final TagKey<Item> INGOTS_COPPER = CraftingUtil.getTagKey(ForgeRegistries.Keys.ITEMS, new ResourceLocation(FORGE, "ingots/copper"));
	public static final TagKey<Item> INGOTS_BRONZE = CraftingUtil.getTagKey(ForgeRegistries.Keys.ITEMS, new ResourceLocation(FORGE, "ingots/bronze"));
	public static final TagKey<Item> GEMS_PURE_QUARTZ = CraftingUtil.getTagKey(ForgeRegistries.Keys.ITEMS, new ResourceLocation(CR, "gems/pure_quartz"));
	public static final TagKey<Item> GEMS_RUBY = CraftingUtil.getTagKey(ForgeRegistries.Keys.ITEMS, new ResourceLocation(FORGE, "gems/ruby"));
	public static final TagKey<Item> GEMS_VOID = CraftingUtil.getTagKey(ForgeRegistries.Keys.ITEMS, new ResourceLocation(CR, "gems/void"));
	public static final TagKey<Item> EXPLODE_IF_KNOCKED = CraftingUtil.getTagKey(ForgeRegistries.Keys.ITEMS, new ResourceLocation(CR, "explode_on_hit"));
	public static final TagKey<Item> SALT_REACTOR_COOLANT = CraftingUtil.getTagKey(ForgeRegistries.Keys.ITEMS, new ResourceLocation(CR, "salt_reactor_coolant"));
}

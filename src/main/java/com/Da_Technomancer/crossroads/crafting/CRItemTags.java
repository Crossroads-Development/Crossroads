package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class CRItemTags{

	private static final String FORGE = "forge";
	private static final String CR = Crossroads.MODID;


	public static final TagKey<Item> INGOTS_COPPER = CraftingUtil.getTagKey(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FORGE, "ingots/copper"));
	public static final TagKey<Item> INGOTS_BRONZE = CraftingUtil.getTagKey(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FORGE, "ingots/bronze"));
	public static final TagKey<Item> GEMS_PURE_QUARTZ = CraftingUtil.getTagKey(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(CR, "gems/pure_quartz"));
	public static final TagKey<Item> GEMS_RUBY = CraftingUtil.getTagKey(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(FORGE, "gems/ruby"));
	public static final TagKey<Item> GEMS_VOID = CraftingUtil.getTagKey(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(CR, "gems/void"));
	public static final TagKey<Item> EXPLODE_IF_KNOCKED = CraftingUtil.getTagKey(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(CR, "explode_on_hit"));
	public static final TagKey<Item> SALT_REACTOR_COOLANT = CraftingUtil.getTagKey(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(CR, "salt_reactor_coolant"));
}

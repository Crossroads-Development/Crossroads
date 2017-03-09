package com.Da_Technomancer.crossroads.integration.GuideAPI;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.enums.GearTypes;
import com.Da_Technomancer.crossroads.API.enums.HeatConductors;
import com.Da_Technomancer.crossroads.API.enums.HeatInsulators;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.items.itemSets.HeatCableFactory;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetUp;

import amerifrance.guideapi.api.GuideAPI;
import amerifrance.guideapi.api.GuideBook;
import amerifrance.guideapi.api.IGuideBook;
import amerifrance.guideapi.api.IPage;
import amerifrance.guideapi.api.impl.Book;
import amerifrance.guideapi.api.impl.Page;
import amerifrance.guideapi.api.impl.abstraction.CategoryAbstract;
import amerifrance.guideapi.api.impl.abstraction.EntryAbstract;
import amerifrance.guideapi.api.util.TextHelper;
import amerifrance.guideapi.category.CategoryItemStack;
import amerifrance.guideapi.entry.EntryItemStack;
import amerifrance.guideapi.page.PageFurnaceRecipe;
import amerifrance.guideapi.page.PageIRecipe;
import amerifrance.guideapi.page.PageImage;
import amerifrance.guideapi.page.PageText;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * TODO I intend to re-write/re-format most of the entries in the Technician's Manual to be better organized and more concise. This will not happen all in one update.
 */
public final class GuideBooks{

	public static final Book MAIN = new Book();
	public static final Book INFO = new Book();
	protected static boolean smallText;

	@GuideBook
	public static class MainGuide implements IGuideBook{

		@Nullable
		@Override
		public Book buildBook(){

			smallText = ModConfig.smallText.getBoolean();

			// Technomancer = normal (§r§r), use the double §r to make createPages() work
			// Witch = underline (§r§n)
			// Alchemist = italic (§r§o)
			// Bobo = bold (§r§l)
			// The § symbol can be typed by holding alt and typing 0167 on the numpad, then releasing alt.

			LinkedHashMap<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<ResourceLocation, EntryAbstract>();
			ArrayList<IPage> pages = new ArrayList<IPage>();
			ArrayList<CategoryAbstract> categories = new ArrayList<CategoryAbstract>();

			// INTRO
			createPages(pages, "lore.first_read");
			entries.put(new ResourceLocation(Main.MODID, "first_read"), new EntryItemStack(pages, "READ ME FIRST", new ItemStack(Items.BOOK, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.intro.start", new ShapelessOreRecipe(Items.WRITTEN_BOOK, Items.BOOK, Items.COMPASS), "lore.intro.end");
			entries.put(new ResourceLocation(Main.MODID, "intro"), new EntryItemStack(pages, "Introduction", new ItemStack(Items.PAPER, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.ores.start", new ResourceLocation(Main.MODID, "textures/blocks/ore_native_copper.png"), "lore.ores.copper", new ResourceLocation(Main.MODID, "textures/blocks/ore_copper.png"), "lore.ores.tin", new ResourceLocation(Main.MODID, "textures/blocks/ore_tin.png"), "lore.ores.ruby", new ResourceLocation(Main.MODID, "textures/blocks/ore_ruby.png"), "lore.ores.bronze", new ShapedOreRecipe(OreSetUp.ingotBronze, "###", "#$#", "###", '#', "nuggetCopper", '$', "nuggetTin"), new ShapedOreRecipe(OreSetUp.blockBronze, "###", "#$#", "###", '#', "ingotCopper", '$', "ingotTin"), new ShapedOreRecipe(new ItemStack(OreSetUp.blockBronze, 9), "###", "#?#", "###", '#', "blockCopper", '?', "blockTin"), "lore.ores.salt", new ShapedOreRecipe(new ItemStack(ModBlocks.blockSalt, 1), "##", "##", '#', "dustSalt"), new ShapedOreRecipe(new ItemStack(ModItems.dustSalt, 4), "#", '#', "blockSalt"), new ShapedOreRecipe(new ItemStack(ModBlocks.blockSaltTile, 4), "##", "##", '#', "blockSalt"));
			entries.put(new ResourceLocation(Main.MODID, "ores"), new EntryItemStack(pages, "Ores", new ItemStack(OreSetUp.ingotCopper, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.energy");
			entries.put(new ResourceLocation(Main.MODID, "energy"), new EntryItemStack(pages, "Basics of Energy", new ItemStack(ModItems.handCrank, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.heat.start", new ShapedOreRecipe(new ItemStack(HeatCableFactory.HEAT_CABLES.get(HeatConductors.COPPER).get(HeatInsulators.WOOL), 4), "###", "$$$", "###", '#', Blocks.WOOL, '$', "ingotCopper"), "lore.heat.wires", new ShapedOreRecipe(new ItemStack(ModItems.diamondWire, 3), "*&*", '*', "ingotTin", '&', "gemDiamond"), "lore.heat_thermometer", new ShapedOreRecipe(new ItemStack(ModItems.thermometer, 1), "#", "$", "?", '#', "dyeRed", '$', ModBlocks.axle, '?', "blockGlass"), "lore.heat.bobo");
			entries.put(new ResourceLocation(Main.MODID, "heat"), new EntryItemStack(pages, "Basics of Heat", new ItemStack(ModItems.thermometer, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.steam.start", new ShapedOreRecipe(new ItemStack(ModBlocks.fluidTube, 8), "###", "   ", "###", '#', "ingotBronze"), "lore.steam.tubes", new ShapedOreRecipe(new ItemStack(ModBlocks.fluidTank, 1), " $ ", "$#$", " $ ", '#', "ingotGold", '$', "ingotBronze"), "lore.steam.steam", new ShapedOreRecipe(new ItemStack(ModBlocks.steamBoiler, 1), "###", "# #", "&&&", '#', "ingotBronze", '&', "ingotCopper"), Pair.of("lore.steam.boiler", new Object[] {Math.round(EnergyConverters.DEG_PER_BUCKET_STEAM * 1.1D), EnergyConverters.DEG_PER_BUCKET_STEAM}), new ShapedOreRecipe(new ItemStack(ModItems.fluidGauge, 1), " * ", "*#*", " *$", '#', "blockGlass", '*', "ingotIron", '$', ModBlocks.fluidTube));
			entries.put(new ResourceLocation(Main.MODID, "steam"), new EntryItemStack(pages, "Basics of Steam", new ItemStack(ModItems.fluidGauge, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.rotary.start", new ShapedOreRecipe(new ItemStack(ModBlocks.masterAxis, 1), "###", "# #", "#$#", '#', "ingotIron", '$', "stickIron"), "lore.rotary.small_gears", new ShapedOreRecipe(new ItemStack(GearFactory.BASIC_GEARS.get(GearTypes.GOLD), 9), " * ", "*&*", " * ", '*', "ingotGold", '&', "blockGold"), new ShapedOreRecipe(new ItemStack(GearFactory.BASIC_GEARS.get(GearTypes.GOLD), 1), " * ", "*&*", " * ", '*', "nuggetGold", '&', "ingotGold"), "lore.rotary_large_gears", new ShapedOreRecipe(new ItemStack(GearFactory.LARGE_GEARS.get(GearTypes.GOLD), 1), "***", "*&*", "***", '*', GearFactory.BASIC_GEARS.get(GearTypes.GOLD), '&', "blockGold"), "lore.rotary.hand_crank", new ShapedOreRecipe(new ItemStack(ModItems.handCrank, 1), " ?", "##", "$ ", '?', Blocks.LEVER, '#', "stickWood", '$', "cobblestone"), "lore.rotary.speedometer", new ShapedOreRecipe(new ItemStack(ModItems.speedometer, 1), "#", "$", '#', "string", '$', Items.COMPASS), "lore.rotary.omnimeter", new ShapedOreRecipe(new ItemStack(ModItems.omnimeter, 1), " # ", "&$%", " ? ", '#', ModItems.fluidGauge, '&', ModItems.thermometer, '$', "gemEmerald", '%', ModItems.speedometer, '?', Items.CLOCK));
			entries.put(new ResourceLocation(Main.MODID, "rotary"), new EntryItemStack(pages, "Basics of Rotary", new ItemStack(ModItems.speedometer, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.copper", new ResourceLocation(Main.MODID, "textures/book/copper_process.png"));
			entries.put(new ResourceLocation(Main.MODID, "copper"), new EntryItemStack(pages, "Copper Processing", new ItemStack(OreSetUp.ingotCopper, 1), smallText));
			pages = new ArrayList<IPage>();

			categories.add(new CategoryItemStack(entries, "The Basics", new ItemStack(OreSetUp.oreCopper, 1)));
			entries = new LinkedHashMap<ResourceLocation, EntryAbstract>();

			// HEAT
			createPages(pages, "lore.coal_heater.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.coalHeater, 1), "#*#", "# #", "###", '#', "cobblestone", '*', "ingotCopper"), "lore.coal_heater.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "coal_heater"), new EntryItemStack(pages, "Coal Heater", new ItemStack(ModBlocks.coalHeater, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.heating_chamber.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.heatingChamber, 1), "#*#", "# #", "###", '#', "ingotIron", '*', "ingotCopper"), "lore.heating_chamber.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "heating_chamber"), new EntryItemStack(pages, "Heating Chamber", new ItemStack(ModBlocks.heatingChamber, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.heat_exchanger.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.heatExchanger, 1), "#$#", "$$$", "###", '#', Blocks.IRON_BARS, '$', "ingotCopper"), "lore.heat_exchanger.mid_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.insulHeatExchanger, 1), "###", "#$#", "###", '#', "obsidian", '$', ModBlocks.heatExchanger), "lore.heat_exchanger.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "heat_exchanger"), new EntryItemStack(pages, "Heat Exchangers", new ItemStack(ModBlocks.heatExchanger, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.heating_crucible.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.heatingCrucible, 1), "# #", "#?#", "###", '#', Blocks.HARDENED_CLAY, '?', Items.CAULDRON), "lore.heating_crucible.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "heating_crucible"), new EntryItemStack(pages, "Heating Crucible", new ItemStack(ModBlocks.heatingCrucible, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.fluid_cooling_chamber", new ShapedOreRecipe(new ItemStack(ModBlocks.fluidCoolingChamber, 1), "###", "# #", "%%%", '#', "ingotTin", '%', "ingotIron"));
			entries.put(new ResourceLocation(Main.MODID, "fluid_cooling"), new EntryItemStack(pages, "Fluid Cooling Chamber", new ItemStack(ModBlocks.fluidCoolingChamber, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.redstone_cable.pre_recipe", new ShapedOreRecipe(new ItemStack(HeatCableFactory.REDSTONE_HEAT_CABLES.get(HeatConductors.COPPER).get(HeatInsulators.WOOL), 1), "###", "#?#", "###", '#', "dustRedstone", '?', HeatCableFactory.HEAT_CABLES.get(HeatConductors.COPPER).get(HeatInsulators.WOOL)), "lore.redstone_cable.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "redstone_cable"), new EntryItemStack(pages, "Redstone Heat Cables", new ItemStack(Items.REDSTONE, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.salt_reactor.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.saltReactor, 1), "#$#", "$%$", "#@#", '#', "ingotTin", '$', ModBlocks.fluidTube, '%', "blockSalt", '@', "ingotCopper"), "lore.salt_reactor.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "salt_reactor"), new EntryItemStack(pages, "Salt Reactor", new ItemStack(ModBlocks.saltReactor, 1), smallText));
			pages = new ArrayList<IPage>();

			categories.add(new CategoryItemStack(entries, "Heat Machines", new ItemStack(ModBlocks.heatingChamber, 1)));
			entries = new LinkedHashMap<ResourceLocation, EntryAbstract>();

			// ROTARY
			createPages(pages, "lore.axles.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.axle, 1), "#", "?", "#", '#', Blocks.STONE, '?', "ingotIron"), "lore.axles.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "axles"), new EntryItemStack(pages, "Axles", new ItemStack(ModBlocks.axle, 1), true));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.grindstone.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.grindstone, 1), "#$#", "#?#", "#$#", '#', "cobblestone", '?', "stickIron", '$', Blocks.PISTON), "lore.grindstone.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "grindstone"), new EntryItemStack(pages, "Grindstone", new ItemStack(ModBlocks.grindstone, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.item_chute.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.itemChute, 4), "#$#", "#$#", "#$#", '#', "ingotIron", '$', "stickIron"), new ShapelessOreRecipe(new ItemStack(ModBlocks.itemChutePort, 1), ModBlocks.itemChute, Blocks.IRON_TRAPDOOR), "lore.item_chute.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "item_chute"), new EntryItemStack(pages, "Item Chutes", new ItemStack(ModBlocks.itemChutePort, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.drill.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.rotaryDrill, 2), " * ", "*#*", '*', "ingotIron", '#', "blockIron"), "lore.drill.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "drill"), new EntryItemStack(pages, "Rotary Drill", new ItemStack(ModBlocks.rotaryDrill, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.toggle_gear.pre_recipe", new ShapelessOreRecipe(new ItemStack(GearFactory.TOGGLE_GEARS.get(GearTypes.GOLD), 1), "dustRedstone", "dustRedstone", "stickIron", GearFactory.BASIC_GEARS.get(GearTypes.GOLD)), "lore.toggle_gear.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "toggle_gear"), new EntryItemStack(pages, "Toggle Gear", new ItemStack(Items.REDSTONE, 1), smallText));
			pages = new ArrayList<IPage>();

			categories.add(new CategoryItemStack(entries, "Rotary Machines", new ItemStack(GearFactory.BASIC_GEARS.get(GearTypes.BRONZE), 1)));
			entries = new LinkedHashMap<ResourceLocation, EntryAbstract>();

			// FLUIDS
			createPages(pages, "lore.rotary_pump.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.rotaryPump, 1), "#$#", "#$#", "&$&", '#', "ingotBronze", '&', "blockGlass", '$', "stickIron"), "lore.rotary_pump.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "rotary_pump"), new EntryItemStack(pages, "Rotary Pump", new ItemStack(ModBlocks.rotaryPump, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.steam_turbine.pre_recipe", new ShapelessOreRecipe(new ItemStack(ModBlocks.steamTurbine, 1), ModBlocks.rotaryPump), new ShapelessOreRecipe(new ItemStack(ModBlocks.rotaryPump, 1), ModBlocks.steamTurbine), Pair.of("lore.steam_turbine.post_recipe", new Object[] {EnergyConverters.DEG_PER_BUCKET_STEAM / EnergyConverters.DEG_PER_JOULE}));
			entries.put(new ResourceLocation(Main.MODID, "steam_turbine"), new EntryItemStack(pages, "Steam Turbine", new ItemStack(ModBlocks.steamTurbine, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, Pair.of("lore.radiator.pre_recipe", new Object[] {EnergyConverters.DEG_PER_BUCKET_STEAM}), new ShapedOreRecipe(new ItemStack(ModBlocks.radiator, 1), "#$#", "#$#", "#$#", '#', ModBlocks.fluidTube, '$', "ingotIron"), "lore.radiator.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "radiator"), new EntryItemStack(pages, "Radiator", new ItemStack(ModBlocks.radiator, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.liquid_fat.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.fatCollector, 1), "***", "# #", "*&*", '*', "ingotBronze", '#', "netherrack", '&', "ingotCopper"), Pair.of("lore.liquid_fat.mid_recipe", new Object[] {EnergyConverters.FAT_PER_VALUE}), new ShapedOreRecipe(new ItemStack(ModBlocks.fatCongealer, 1), "*^*", "# #", "* *", '*', "ingotBronze", '#', "netherrack", '^', "stickIron"), "lore.liquid_fat.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "liquid_fat"), new EntryItemStack(pages, "Basics of Liquid Fat", new ItemStack(ModItems.edibleBlob, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.fat_feeder.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.fatFeeder, 1), "*^*", "#&#", "*^*", '*', "ingotBronze", '#', "netherrack", '^', "stickIron", '&', "ingotTin"), Pair.of("lore.fat_feeder.post_recipe", new Object[]{EnergyConverters.FAT_PER_VALUE}));
			entries.put(new ResourceLocation(Main.MODID, "fat_feeder"), new EntryItemStack(pages, "Fat Feeder", new ItemStack(ModBlocks.fatFeeder, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.redstone_tube", new ShapedOreRecipe(new ItemStack(ModBlocks.redstoneFluidTube, 1), "***", "*&*", "***", '*', "dustRedstone", '&', ModBlocks.fluidTube));
			entries.put(new ResourceLocation(Main.MODID, "redstone_tube"), new EntryItemStack(pages, "Redstone Integration-Fluids", new ItemStack(Items.REDSTONE), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.water_centrifuge.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.waterCentrifuge, 1), "*&*", "^%^", "* *", '*', "ingotBronze", '&', "stickIron", '^', ModBlocks.fluidTube, '%', "ingotTin"), "lore.water_centrifuge.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "water_centrifuge"), new EntryItemStack(pages, "Water Centrifuge", new ItemStack(ModBlocks.waterCentrifuge), smallText));
			pages = new ArrayList<IPage>();

			categories.add(new CategoryItemStack(entries, "Fluid Machines", new ItemStack(ModBlocks.fluidTube, 1)));
			entries = new LinkedHashMap<ResourceLocation, EntryAbstract>();

			// MISC
			createPages(pages, "lore.brazier.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.brazier, 1), "###", " $ ", " $ ", '$', "stoneAndesitePolished", '#', "stoneAndesite"), "lore.brazier.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "brazier"), new EntryItemStack(pages, "Brazier", new ItemStack(ModBlocks.brazier, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.item_sorting.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.sortingHopper, 1), "# #", "#&#", " # ", '#', "ingotCopper", '&', "chestWood"), new ShapedOreRecipe(new ItemStack(ModBlocks.sortingHopper, 1), "#&#", "###", '#', "ingotCopper", '&', "chestWood"), new ShapedOreRecipe(new ItemStack(ModBlocks.slottedChest, 1), "###", "$@$", "###", '#', "slabWood", '$', Blocks.TRAPDOOR, '@', "chestWood"), "lore.item_sorting.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "item_sorting"), new EntryItemStack(pages, "Sorting Devices", new ItemStack(ModBlocks.sortingHopper, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.ob_cutting.pre_recipe", new ShapedOreRecipe(new ItemStack(ModItems.obsidianKit, 4), " # ", "#$#", " # ", '$', "obsidian", '#', Items.FLINT), "lore.ob_cutting.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "ob_cutting"), new EntryItemStack(pages, "Obsidian Cutting Kits", new ItemStack(ModItems.obsidianKit, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.decorative.pre_recipe", new ShapelessOreRecipe(new ItemStack(ModBlocks.candleLilyPad), Blocks.WATERLILY, "torch"), "lore.decorative.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "decorative"), new EntryItemStack(pages, "Decorative Blocks", new ItemStack(ModItems.itemCandleLilypad, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.fertile_soil.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.fertileSoil, 3, 0), "#$#", "***", "^^^", '#', new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', "cropWheat"), "lore.fertile_soil.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "fertile_soil"), new EntryItemStack(pages, "Fertile Soil", new ItemStack(ModBlocks.fertileSoil, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.multi_piston.pre_recipe", new ShapedOreRecipe(ModBlocks.multiPiston, "***", "$#$", "$$$", '*', "ingotTin", '$', "ingotBronze", '#', Blocks.PISTON), new ShapedOreRecipe(ModBlocks.multiPistonSticky, "***", "$#$", "$$$", '*', "ingotTin", '$', "ingotBronze", '#', Blocks.STICKY_PISTON), new ShapelessOreRecipe(ModBlocks.multiPistonSticky, ModBlocks.multiPiston, "slimeball"), "lore.multi_piston.post_recipe", new ShapelessOreRecipe(Blocks.PISTON, "cobblestone", "ingotIron", "dustRedstone", "logWood"));
			entries.put(new ResourceLocation(Main.MODID, "multi_piston"), new EntryItemStack(pages, "Multi-Piston", new ItemStack(ModBlocks.multiPiston, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.ratiator.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.ratiator, 1), " * ", "*#*", "^^^", '*', ModItems.luminescentQuartz, '#', ModItems.pureQuartz, '^', "stone"), "lore.ratiator.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "ratiator"), new EntryItemStack(pages, "Ratiator", new ItemStack(ModBlocks.ratiator, 1), smallText));
			pages = new ArrayList<IPage>();

			categories.add(new CategoryItemStack(entries, "Miscellaneous", new ItemStack(ModBlocks.brazier, 1)));
			entries = new LinkedHashMap<ResourceLocation, EntryAbstract>();

			//MAGIC
			createPages(pages, "lore.basic_magic.pre_recipe", new ShapelessOreRecipe(new ItemStack(ModItems.pureQuartz, 1), "dustSalt", "dustSalt", "gemQuartz"), new ShapedOreRecipe(new ItemStack(ModBlocks.blockPureQuartz, 1), "**", "**", '*', ModItems.pureQuartz), new ShapelessOreRecipe(new ItemStack(ModItems.pureQuartz, 4), ModBlocks.blockPureQuartz), "lore.basic_magic.mid_recipe", new ShapedOreRecipe(new ItemStack(ModItems.lensArray, 2), "*&*", "@#$", "***", '*', ModItems.pureQuartz, '&', "gemEmerald", '@', "gemRuby", '$', "gemDiamond"), "lore.basic_magic.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "basic_magic"), new EntryItemStack(pages, "Basics of Magic", new ItemStack(ModItems.pureQuartz, 1), smallText));
			pages = new ArrayList<IPage>();
			entries.put(new ResourceLocation(Main.MODID, "elements"), new ElementEntry(pages, "Magical Elements", new ItemStack(ModItems.lensArray), true));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.color_chart.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.colorChart, 1), "RGB", "^^^", "___", '_', "slabWood", '^', "paper", 'R', "dyeRed", 'G', "dyeLime", 'B', "dyeBlue"), "lore.color_chart.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "color_chart"), new EntryItemStack(pages, "Discovering Elements", new ItemStack(ModBlocks.colorChart, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.arcane_extractor.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.arcaneExtractor, 1), "***", "*# ", "***", '*', "stone", '#', ModItems.lensArray), "lore.arcane_extractor.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "arcane_extractor"), new EntryItemStack(pages, "Arcane Extractor", new ItemStack(ModBlocks.arcaneExtractor, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.quartz_stabilizer.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.smallQuartzStabilizer, 1), " * ", "*&*", "***", '*', ModItems.pureQuartz, '&', ModItems.lensArray), new ShapedOreRecipe(new ItemStack(ModBlocks.largeQuartzStabilizer, 1), "***", "*&*", "***", '*', ModItems.pureQuartz, '&', ModBlocks.smallQuartzStabilizer), "lore.quartz_stabilizer.mid_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.smallQuartzStabilizer, 1), " & ", "***", '&', ModItems.luminescentQuartz, '*', ModItems.pureQuartz), "lore.quartz_stabilizer.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "quartz_stabilizer"), new EntryItemStack(pages, "Quartz Stabilizer", new ItemStack(ModBlocks.smallQuartzStabilizer, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.lens_holder.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.lensHolder, 1), "***", "*&*", "***", '*', "stone", '&', ModItems.pureQuartz), "lore.lens_holder.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "lens_holder"), new EntryItemStack(pages, "Lens Holder", new ItemStack(ModBlocks.lensHolder, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.arcane_reflector", new ShapedOreRecipe(new ItemStack(ModBlocks.arcaneReflector, 1), "*^*", "^^^", "*^*", '*', "stone", '^', ModItems.pureQuartz));
			entries.put(new ResourceLocation(Main.MODID, "arcane_reflector"), new EntryItemStack(pages, "Arcane Reflector", new ItemStack(ModBlocks.arcaneReflector, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.beam_splitter", new ShapedOreRecipe(new ItemStack(ModBlocks.beamSplitterBasic, 1), "*^*", "*&*", "*^*", '*', ModItems.pureQuartz, '^', ModItems.luminescentQuartz, '&', ModItems.lensArray), new ShapelessOreRecipe(new ItemStack(ModBlocks.beamSplitter, 1), ModBlocks.beamSplitterBasic, "dustRedstone", "dustRedstone", "dustRedstone"));
			entries.put(new ResourceLocation(Main.MODID, "beam_splitter"), new EntryItemStack(pages, "Beam Splitter", new ItemStack(ModBlocks.beamSplitter, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.crystalline_prism", new ShapedOreRecipe(new ItemStack(ModBlocks.crystallinePrism, 1), "*^*", "^&^", "*&*", '*', ModItems.pureQuartz, '^', ModItems.luminescentQuartz, '&', ModItems.lensArray));
			entries.put(new ResourceLocation(Main.MODID, "crystalline_prism"), new EntryItemStack(pages, "Crystalline Prism", new ItemStack(ModBlocks.crystallinePrism, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.crystal_master_axis.pre_recipe", new ShapedOreRecipe(ModBlocks.crystalMasterAxis, "*&*", "*#*", "***", '*', ModItems.pureQuartz, '#', ModBlocks.masterAxis, '&', ModItems.lensArray), "lore.crystal_master_axis.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "crystal_master_axis"), new EntryItemStack(pages, "Crystalline Master Axis", new ItemStack(ModBlocks.crystalMasterAxis, 1), true));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.void.pre_recipe", new ShapedOreRecipe(new ItemStack(ModItems.voidCrystal, 1), "*#*", "###", "*#*", '*', Items.DRAGON_BREATH, '#', ModItems.pureQuartz), "lore.void.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "void"), new EntryItemStack(pages, "Void", new ItemStack(ModItems.voidCrystal, 1), true));
			pages = new ArrayList<IPage>();
			createPages(pages, "lore.beacon_harness.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.beaconHarness, 1), "*&*", "&^&", "*&*", '*', ModItems.pureQuartz, '&', ModItems.lensArray, '^', ModItems.luminescentQuartz), "lore.beacon_harness.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "beacon_harness"), new EntryItemStack(pages, "Beacon Harness", new ItemStack(ModBlocks.beaconHarness, 1), true));
			pages = new ArrayList<IPage>();

			categories.add(new CategoryItemStack(entries, "Magic", new ItemStack(ModItems.lensArray, 1)));
			entries = new LinkedHashMap<ResourceLocation, EntryAbstract>();

			MAIN.setTitle("Main Menu");
			MAIN.setWelcomeMessage("Welcome to Crossroads");
			MAIN.setDisplayName("mysterious_journal");
			MAIN.setColor(Color.GRAY);
			MAIN.setCategoryList(categories);
			MAIN.setRegistryName(new ResourceLocation(Main.MODID, "crossroadsMainGuide"));
			MAIN.setSpawnWithBook(true);
			return MAIN;
		}

		@Override
		public void handleModel(ItemStack bookStack){
			GuideAPI.setModel(MAIN);

		}

		@Override
		public void handlePost(ItemStack bookStack){
			GameRegistry.addShapelessRecipe(bookStack, Items.BOOK, Items.COMPASS);
			GameRegistry.addShapelessRecipe(bookStack, GuideAPI.getStackFromBook(GuideBooks.INFO));
		}
	}

	@GuideBook
	public static class InfoGuide implements IGuideBook{

		@Override
		public Book buildBook(){
			LinkedHashMap<ResourceLocation, EntryAbstract> entries = new LinkedHashMap<ResourceLocation, EntryAbstract>();
			ArrayList<IPage> pages = new ArrayList<IPage>();
			ArrayList<CategoryAbstract> categories = new ArrayList<CategoryAbstract>();
			
			// INTRO
			createPages(pages, "info.first_read");
			entries.put(new ResourceLocation(Main.MODID, "first_read"), new EntryItemStack(pages, "READ ME FIRST", new ItemStack(Items.BOOK, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.intro.pre_recipe", new ShapelessOreRecipe(Items.WRITTEN_BOOK, Items.BOOK, Items.COMPASS), "info.intro.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "intro"), new EntryItemStack(pages, "Introduction", new ItemStack(Items.PAPER, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.ores.native_copper", new ResourceLocation(Main.MODID, "textures/blocks/ore_native_copper.png"), "info.ores.copper", new ResourceLocation(Main.MODID, "textures/blocks/ore_copper.png"), "info.ores.tin", new ResourceLocation(Main.MODID, "textures/blocks/ore_tin.png"), "info.ores.ruby", new ResourceLocation(Main.MODID, "textures/blocks/ore_ruby.png"), "info.ores.bronze", new ShapedOreRecipe(OreSetUp.ingotBronze, "###", "#$#", "###", '#', "nuggetCopper", '$', "nuggetTin"), new ShapedOreRecipe(OreSetUp.blockBronze, "###", "#$#", "###", '#', "ingotCopper", '$', "ingotTin"), new ShapedOreRecipe(new ItemStack(OreSetUp.blockBronze, 9), "###", "#?#", "###", '#', "blockCopper", '?', "blockTin"), "info.ores.salt", new ShapedOreRecipe(new ItemStack(ModBlocks.blockSalt, 1), "##", "##", '#', "dustSalt"), new ShapedOreRecipe(new ItemStack(ModItems.dustSalt, 4), "#", '#', "blockSalt"), new ShapedOreRecipe(new ItemStack(ModBlocks.blockSaltTile, 4), "##", "##", '#', "blockSalt"));
			entries.put(new ResourceLocation(Main.MODID, "ores"), new EntryItemStack(pages, "Ores", new ItemStack(OreSetUp.ingotCopper, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.energy");
			entries.put(new ResourceLocation(Main.MODID, "energy"), new EntryItemStack(pages, "Basics of Energy", new ItemStack(ModItems.handCrank, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.heat.pre_recipe", new ShapedOreRecipe(new ItemStack(HeatCableFactory.HEAT_CABLES.get(HeatConductors.COPPER).get(HeatInsulators.WOOL), 4), "###", "$$$", "###", '#', Blocks.WOOL, '$', "ingotCopper"), "info.heat.mid_recipe",new ShapedOreRecipe(new ItemStack(ModItems.diamondWire, 3), "*&*", '*', "ingotTin", '&', "gemDiamond"), "info.heat.post_recipe", new ShapedOreRecipe(new ItemStack(ModItems.thermometer, 1), "#", "$", "?", '#', "dyeRed", '$', ModBlocks.axle, '?', "blockGlass"));
			entries.put(new ResourceLocation(Main.MODID, "heat"), new EntryItemStack(pages, "Basics of Heat", new ItemStack(ModItems.thermometer, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.steam.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.fluidTube, 8), "###", "   ", "###", '#', "ingotBronze"), "info.steam.mid_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.fluidTank, 1), " $ ", "$#$", " $ ", '#', "ingotGold", '$', "ingotBronze"), "info.steam.pre_boiler_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.steamBoiler, 1), "###", "# #", "&&&", '#', "ingotBronze", '&', "ingotCopper"), Pair.of("info.steam.boiler", new Object[] {Math.round(EnergyConverters.DEG_PER_BUCKET_STEAM * 1.1D), EnergyConverters.DEG_PER_BUCKET_STEAM}), new ShapedOreRecipe(new ItemStack(ModItems.fluidGauge, 1), " * ", "*#*", " *$", '#', "blockGlass", '*', "ingotIron", '$', ModBlocks.fluidTube));
			entries.put(new ResourceLocation(Main.MODID, "steam"), new EntryItemStack(pages, "Basics of Steam", new ItemStack(ModItems.fluidGauge, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.rotary.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.masterAxis, 1), "###", "# #", "#$#", '#', "ingotIron", '$', "stickIron"), "info.rotary.mid_recipe", new ShapedOreRecipe(new ItemStack(GearFactory.BASIC_GEARS.get(GearTypes.GOLD), 9), " * ", "*&*", " * ", '*', "ingotGold", '&', "blockGold"), new ShapedOreRecipe(new ItemStack(GearFactory.BASIC_GEARS.get(GearTypes.GOLD), 1), " * ", "*&*", " * ", '*', "nuggetGold", '&', "ingotGold"), "info.rotary.large_gear", new ShapedOreRecipe(new ItemStack(GearFactory.LARGE_GEARS.get(GearTypes.GOLD), 1), "***", "*&*", "***", '*', GearFactory.BASIC_GEARS.get(GearTypes.GOLD), '&', "blockGold"), "info.rotary.gear_connections", new ShapedOreRecipe(new ItemStack(ModItems.handCrank, 1), " ?", "##", "$ ", '?', Blocks.LEVER, '#', "stickWood", '$', "cobblestone"), "info.rotary.hand_crank", new ShapedOreRecipe(new ItemStack(ModItems.speedometer, 1), "#", "$", '#', "string", '$', Items.COMPASS), "info.rotary.omnimeter", new ShapedOreRecipe(new ItemStack(ModItems.omnimeter, 1), " # ", "&$%", " ? ", '#', ModItems.fluidGauge, '&', ModItems.thermometer, '$', "gemEmerald", '%', ModItems.speedometer, '?', Items.CLOCK));
			entries.put(new ResourceLocation(Main.MODID, "rotary"), new EntryItemStack(pages, "Basics of Rotary", new ItemStack(ModItems.speedometer, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.copper", new ResourceLocation(Main.MODID, "textures/book/copper_process.png"));
			entries.put(new ResourceLocation(Main.MODID, "copper"), new EntryItemStack(pages, "Copper Processing", new ItemStack(OreSetUp.ingotCopper, 1), smallText));
			pages = new ArrayList<IPage>();


			categories.add(new CategoryItemStack(entries, "The Basics", new ItemStack(OreSetUp.oreCopper, 1)));
			entries = new LinkedHashMap<ResourceLocation, EntryAbstract>();

			// HEAT
			createPages(pages, "info.coal_heater.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.coalHeater, 1), "#*#", "# #", "###", '#', "cobblestone", '*', "ingotCopper"), "info.coal_heater.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "coal_heater"), new EntryItemStack(pages, "Coal Heater", new ItemStack(ModBlocks.coalHeater, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.heating_chamber.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.heatingChamber, 1), "#*#", "# #", "###", '#', "ingotIron", '*', "ingotCopper"), "info.heating_chamber.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "heating_chamber"), new EntryItemStack(pages, "Heating Chamber", new ItemStack(ModBlocks.heatingChamber, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.heat_exchanger.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.heatExchanger, 1), "#$#", "$$$", "###", '#', Blocks.IRON_BARS, '$', "ingotCopper"), "info.heat_exchanger.mid_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.insulHeatExchanger, 1), "###", "#$#", "###", '#', "obsidian", '$', ModBlocks.heatExchanger), "info.heat_exchanger.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "heat_exchanger"), new EntryItemStack(pages, "Heat Exchangers", new ItemStack(ModBlocks.heatExchanger, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.heating_crucible.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.heatingCrucible, 1), "# #", "#?#", "###", '#', Blocks.HARDENED_CLAY, '?', Items.CAULDRON), "info.heating_crucible.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "heating_crucible"), new EntryItemStack(pages, "Heating Crucible", new ItemStack(ModBlocks.heatingCrucible, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.fluid_cooling", new ShapedOreRecipe(new ItemStack(ModBlocks.fluidCoolingChamber, 1), "###", "# #", "%%%", '#', "ingotTin", '%', "ingotIron"));
			entries.put(new ResourceLocation(Main.MODID, "fluid_cooling"), new EntryItemStack(pages, "Fluid Cooling Chamber", new ItemStack(ModBlocks.fluidCoolingChamber, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.redstone_cable.pre_recipe", new ShapedOreRecipe(new ItemStack(HeatCableFactory.REDSTONE_HEAT_CABLES.get(HeatConductors.COPPER).get(HeatInsulators.WOOL), 1), "###", "#?#", "###", '#', "dustRedstone", '?', HeatCableFactory.HEAT_CABLES.get(HeatConductors.COPPER).get(HeatInsulators.WOOL)), "info.redstone_cable.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "redstone_cable"), new EntryItemStack(pages, "Redstone Heat Cable", new ItemStack(Items.REDSTONE, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.salt_reactor.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.saltReactor, 1), "#$#", "$%$", "#@#", '#', "ingotTin", '$', ModBlocks.fluidTube, '%', "blockSalt", '@', "ingotCopper"), "info.salt_reactor.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "salt_reactor"), new EntryItemStack(pages, "Salt Reactor", new ItemStack(ModBlocks.saltReactor, 1), smallText));
			pages = new ArrayList<IPage>();

			categories.add(new CategoryItemStack(entries, "Heat Machines", new ItemStack(ModBlocks.heatingChamber, 1)));
			entries = new LinkedHashMap<ResourceLocation, EntryAbstract>();

			// ROTARY
			createPages(pages, "info.axles.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.axle, 1), "#", "?", "#", '#', Blocks.STONE, '?', "ingotIron"), "info.axles.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "axles"), new EntryItemStack(pages, "Axles", new ItemStack(ModBlocks.axle, 1), true));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.grindstone.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.grindstone, 1), "#$#", "#?#", "#$#", '#', "cobblestone", '?', "stickIron", '$', Blocks.PISTON), "info.grindstone.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "grindstone"), new EntryItemStack(pages, "Grindstone", new ItemStack(ModBlocks.grindstone, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.item_chute.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.itemChute, 4), "#$#", "#$#", "#$#", '#', "ingotIron", '$', "stickIron"), new ShapelessOreRecipe(new ItemStack(ModBlocks.itemChutePort, 1), ModBlocks.itemChute, Blocks.IRON_TRAPDOOR), "info.item_chute.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "item_chute"), new EntryItemStack(pages, "Item Chutes", new ItemStack(ModBlocks.itemChutePort, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.drill.pre_recipe", new ShapedOreRecipe(new ItemStack(ModBlocks.rotaryDrill, 2), " * ", "*#*", '*', "ingotIron", '#', "blockIron"), "info.drill.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "drill"), new EntryItemStack(pages, "Rotary Drill", new ItemStack(ModBlocks.rotaryDrill, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.toggle_gear.pre_recipe", new ShapelessOreRecipe(new ItemStack(GearFactory.TOGGLE_GEARS.get(GearTypes.GOLD), 1), "dustRedstone", "dustRedstone", "stickIron", GearFactory.BASIC_GEARS.get(GearTypes.GOLD)), "info.toggle_gear.post_recipe");
			entries.put(new ResourceLocation(Main.MODID, "toggle_gear"), new EntryItemStack(pages, "Toggle Gear", new ItemStack(Items.REDSTONE, 1), smallText));
			pages = new ArrayList<IPage>();

			categories.add(new CategoryItemStack(entries, "Rotary Machines", new ItemStack(GearFactory.BASIC_GEARS.get(GearTypes.BRONZE), 1)));
			entries = new LinkedHashMap<ResourceLocation, EntryAbstract>();
			
			// FLUIDS
			createPages(pages,  "info.rotary_pump", new ShapedOreRecipe(new ItemStack(ModBlocks.rotaryPump, 1), "#$#", "#$#", "&$&", '#', "ingotBronze", '&', "blockGlass", '$', "stickIron"));
			entries.put(new ResourceLocation(Main.MODID, "rotary_pump"), new EntryItemStack(pages, "Rotary Pump", new ItemStack(ModBlocks.rotaryPump, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, Pair.of("info.steam_turbine", new Object[] {EnergyConverters.DEG_PER_BUCKET_STEAM / EnergyConverters.DEG_PER_JOULE}), new ShapelessOreRecipe(new ItemStack(ModBlocks.steamTurbine, 1), ModBlocks.rotaryPump), new ShapelessOreRecipe(new ItemStack(ModBlocks.rotaryPump, 1), ModBlocks.steamTurbine));
			entries.put(new ResourceLocation(Main.MODID, "steam_turbine"), new EntryItemStack(pages, "Steam Turbine", new ItemStack(ModBlocks.steamTurbine, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, Pair.of("info.radiator", new Object[] {EnergyConverters.DEG_PER_BUCKET_STEAM}), new ShapedOreRecipe(new ItemStack(ModBlocks.radiator, 1), "#$#", "#$#", "#$#", '#', ModBlocks.fluidTube, '$', "ingotIron"));
			entries.put(new ResourceLocation(Main.MODID, "radiator"), new EntryItemStack(pages, "Radiator", new ItemStack(ModBlocks.radiator, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, Pair.of("info.liquid_fat", new Object[] {EnergyConverters.FAT_PER_VALUE}), new ShapedOreRecipe(new ItemStack(ModBlocks.fatCollector, 1), "***", "# #", "*&*", '*', "ingotBronze", '#', "netherrack", '&', "ingotCopper"), new ShapedOreRecipe(new ItemStack(ModBlocks.fatCongealer, 1), "*^*", "# #", "* *", '*', "ingotBronze", '#', "netherrack", '^', "stickIron"));
			entries.put(new ResourceLocation(Main.MODID, "liquid_fat"), new EntryItemStack(pages, "Basics of Liquid Fat", new ItemStack(ModItems.edibleBlob, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, Pair.of("info.fat_feeder", new Object[] {EnergyConverters.FAT_PER_VALUE}), new ShapedOreRecipe(new ItemStack(ModBlocks.fatFeeder, 1), "*^*", "#&#", "*^*", '*', "ingotBronze", '#', "netherrack", '^', "stickIron", '&', "ingotTin"));
			entries.put(new ResourceLocation(Main.MODID, "fat_feeder"), new EntryItemStack(pages, "Fat Feeder", new ItemStack(ModBlocks.fatFeeder, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.redstone_tube", new ShapedOreRecipe(new ItemStack(ModBlocks.redstoneFluidTube, 1), "***", "*&*", "***", '*', "dustRedstone", '&', ModBlocks.fluidTube));
			entries.put(new ResourceLocation(Main.MODID, "redstone_tube"), new EntryItemStack(pages, "Redstone Integration-Fluids", new ItemStack(Items.REDSTONE), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.water_centrifuge",new ShapedOreRecipe(new ItemStack(ModBlocks.waterCentrifuge, 1), "*&*", "^%^", "* *", '*', "ingotBronze", '&', "stickIron", '^', ModBlocks.fluidTube, '%', "ingotTin"));
			entries.put(new ResourceLocation(Main.MODID, "water_centrifuge"), new EntryItemStack(pages, "Water Centrifuge", new ItemStack(ModBlocks.waterCentrifuge), smallText));
			pages = new ArrayList<IPage>();

			categories.add(new CategoryItemStack(entries, "Fluid Machines", new ItemStack(ModBlocks.fluidTube, 1)));
			entries = new LinkedHashMap<ResourceLocation, EntryAbstract>();

			// MISC
			createPages(pages, "info.brazier", new ShapedOreRecipe(new ItemStack(ModBlocks.brazier, 1), "###", " $ ", " $ ", '$', "stoneAndesitePolished", '#', "stoneAndesite"));
			entries.put(new ResourceLocation(Main.MODID, "brazier"), new EntryItemStack(pages, "Brazier", new ItemStack(ModBlocks.brazier, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.item_sorting", new ShapedOreRecipe(new ItemStack(ModBlocks.sortingHopper, 1), "# #", "#&#", " # ", '#', "ingotCopper", '&', "chestWood"), new ShapedOreRecipe(new ItemStack(ModBlocks.sortingHopper, 1), "#&#", "###", '#', "ingotCopper", '&', "chestWood"), new ShapedOreRecipe(new ItemStack(ModBlocks.slottedChest, 1), "###", "$@$", "###", '#', "slabWood", '$', Blocks.TRAPDOOR, '@', "chestWood"));
			entries.put(new ResourceLocation(Main.MODID, "item_sorting"), new EntryItemStack(pages, "Sorting Devices", new ItemStack(ModBlocks.sortingHopper, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.ob_cutting", new ShapedOreRecipe(new ItemStack(ModItems.obsidianKit, 4), " # ", "#$#", " # ", '$', "obsidian", '#', Items.FLINT));
			entries.put(new ResourceLocation(Main.MODID, "ob_cutting"), new EntryItemStack(pages, "Obsidian Cutting Kits", new ItemStack(ModItems.obsidianKit, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.decorative", new ShapelessOreRecipe(new ItemStack(ModBlocks.candleLilyPad), Blocks.WATERLILY, "torch"));
			entries.put(new ResourceLocation(Main.MODID, "decorative"), new EntryItemStack(pages, "Decorative Blocks", new ItemStack(ModItems.itemCandleLilypad, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.fertile_soil", new ShapedOreRecipe(new ItemStack(ModBlocks.fertileSoil, 3, 0), "#$#", "***", "^^^", '#', new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', "cropWheat"));
			entries.put(new ResourceLocation(Main.MODID, "fertile_soil"), new EntryItemStack(pages, "Fertile Soil", new ItemStack(ModBlocks.fertileSoil, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.multi_piston", new ShapedOreRecipe(ModBlocks.multiPiston, "***", "$#$", "$$$", '*', "ingotTin", '$', "ingotBronze", '#', Blocks.PISTON), new ShapedOreRecipe(ModBlocks.multiPistonSticky, "***", "$#$", "$$$", '*', "ingotTin", '$', "ingotBronze", '#', Blocks.STICKY_PISTON), new ShapelessOreRecipe(ModBlocks.multiPistonSticky, ModBlocks.multiPiston, "slimeball"), new ShapelessOreRecipe(Blocks.PISTON, "cobblestone", "ingotIron", "dustRedstone", "logWood"));
			entries.put(new ResourceLocation(Main.MODID, "multi_piston"), new EntryItemStack(pages, "Multi-Piston", new ItemStack(ModBlocks.multiPiston, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.ratiator", new ShapedOreRecipe(new ItemStack(ModBlocks.ratiator, 1), " * ", "*#*", "^^^", '*', ModItems.luminescentQuartz, '#', ModItems.pureQuartz, '^', "stone"));
			entries.put(new ResourceLocation(Main.MODID, "ratiator"), new EntryItemStack(pages, "Ratiator", new ItemStack(ModBlocks.ratiator, 1), smallText));
			pages = new ArrayList<IPage>();

			categories.add(new CategoryItemStack(entries, "Miscellaneous", new ItemStack(ModBlocks.brazier, 1)));
			entries = new LinkedHashMap<ResourceLocation, EntryAbstract>();

			//MAGIC
			createPages(pages, "info.basic_magic", new ShapelessOreRecipe(new ItemStack(ModItems.pureQuartz, 1), "dustSalt", "dustSalt", "gemQuartz"), new ShapedOreRecipe(new ItemStack(ModBlocks.blockPureQuartz, 1), "**", "**", '*', ModItems.pureQuartz), new ShapelessOreRecipe(new ItemStack(ModItems.pureQuartz, 4), ModBlocks.blockPureQuartz), new ShapedOreRecipe(new ItemStack(ModItems.lensArray, 2), "*&*", "@#$", "***", '*', ModItems.pureQuartz, '&', "gemEmerald", '@', "gemRuby", '$', "gemDiamond"));
			entries.put(new ResourceLocation(Main.MODID, "basic_magic"), new EntryItemStack(pages, "Basics of Magic", new ItemStack(ModItems.pureQuartz, 1), smallText));
			pages = new ArrayList<IPage>();
			entries.put(new ResourceLocation(Main.MODID, "elements"), new ElementEntry(pages, "Magical Elements", new ItemStack(ModItems.lensArray), false));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.color_chart", new ShapedOreRecipe(new ItemStack(ModBlocks.colorChart, 1), "RGB", "^^^", "___", '_', "slabWood", '^', "paper", 'R', "dyeRed", 'G', "dyeLime", 'B', "dyeBlue"));
			entries.put(new ResourceLocation(Main.MODID, "color_chart"), new EntryItemStack(pages, "Discovering Elements", new ItemStack(ModBlocks.colorChart, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.arcane_extractor", new ShapedOreRecipe(new ItemStack(ModBlocks.arcaneExtractor, 1), "***", "*# ", "***", '*', "stone", '#', ModItems.lensArray));
			entries.put(new ResourceLocation(Main.MODID, "arcane_extractor"), new EntryItemStack(pages, "Arcane Extractor", new ItemStack(ModBlocks.arcaneExtractor, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.quartz_stabilizer", new ShapedOreRecipe(new ItemStack(ModBlocks.smallQuartzStabilizer, 1), " * ", "*&*", "***", '*', ModItems.pureQuartz, '&', ModItems.lensArray), new ShapedOreRecipe(new ItemStack(ModBlocks.largeQuartzStabilizer, 1), "***", "*&*", "***", '*', ModItems.pureQuartz, '&', ModBlocks.smallQuartzStabilizer), new ShapedOreRecipe(new ItemStack(ModBlocks.smallQuartzStabilizer, 1), " & ", "***", '&', ModItems.luminescentQuartz, '*', ModItems.pureQuartz));
			entries.put(new ResourceLocation(Main.MODID, "quartz_stabilizer"), new EntryItemStack(pages, "Quartz Stabilizer", new ItemStack(ModBlocks.smallQuartzStabilizer, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.lens_holder", new ShapedOreRecipe(new ItemStack(ModBlocks.lensHolder, 1), "***", "*&*", "***", '*', "stone", '&', ModItems.pureQuartz));
			entries.put(new ResourceLocation(Main.MODID, "lens_holder"), new EntryItemStack(pages, "Lens Holder", new ItemStack(ModBlocks.lensHolder, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.arcane_reflector", new ShapedOreRecipe(new ItemStack(ModBlocks.arcaneReflector, 1), "*^*", "^^^", "*^*", '*', "stone", '^', ModItems.pureQuartz));
			entries.put(new ResourceLocation(Main.MODID, "arcane_reflector"), new EntryItemStack(pages, "Arcane Reflector", new ItemStack(ModBlocks.arcaneReflector, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.beam_splitter", new ShapedOreRecipe(new ItemStack(ModBlocks.beamSplitterBasic, 1), "*^*", "*&*", "*^*", '*', ModItems.pureQuartz, '^', ModItems.luminescentQuartz, '&', ModItems.lensArray), new ShapelessOreRecipe(new ItemStack(ModBlocks.beamSplitter, 1), ModBlocks.beamSplitterBasic, "dustRedstone", "dustRedstone", "dustRedstone"));
			entries.put(new ResourceLocation(Main.MODID, "beam_splitter"), new EntryItemStack(pages, "Beam Splitter", new ItemStack(ModBlocks.beamSplitter, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.crystalline_prism", new ShapedOreRecipe(new ItemStack(ModBlocks.crystallinePrism, 1), "*^*", "^&^", "*&*", '*', ModItems.pureQuartz, '^', ModItems.luminescentQuartz, '&', ModItems.lensArray));
			entries.put(new ResourceLocation(Main.MODID, "crystalline_prism"), new EntryItemStack(pages, "Crystalline Prism", new ItemStack(ModBlocks.crystallinePrism, 1), smallText));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.crystal_master_axis", new ShapedOreRecipe(ModBlocks.crystalMasterAxis, "*&*", "*#*", "***", '*', ModItems.pureQuartz, '#', ModBlocks.masterAxis, '&', ModItems.lensArray));
			entries.put(new ResourceLocation(Main.MODID, "crystal_master_axis"), new EntryItemStack(pages, "Crystalline Master Axis", new ItemStack(ModBlocks.crystalMasterAxis, 1), true));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.void", new ShapedOreRecipe(new ItemStack(ModItems.voidCrystal, 1), "*#*", "###", "*#*", '*', Items.DRAGON_BREATH, '#', ModItems.pureQuartz));
			entries.put(new ResourceLocation(Main.MODID, "void"), new EntryItemStack(pages, "Void", new ItemStack(ModItems.voidCrystal, 1), true));
			pages = new ArrayList<IPage>();
			createPages(pages, "info.beacon_harness", new ShapedOreRecipe(new ItemStack(ModBlocks.beaconHarness, 1), "*&*", "&^&", "*&*", '*', ModItems.pureQuartz, '&', ModItems.lensArray, '^', ModItems.luminescentQuartz));
			entries.put(new ResourceLocation(Main.MODID, "beacon_harness"), new EntryItemStack(pages, "Beacon Harness", new ItemStack(ModBlocks.beaconHarness, 1), true));
			pages = new ArrayList<IPage>();

			categories.add(new CategoryItemStack(entries, "Magic", new ItemStack(ModItems.lensArray, 1)));
			entries = new LinkedHashMap<ResourceLocation, EntryAbstract>();

			INFO.setTitle("Main Menu");
			INFO.setWelcomeMessage("Welcome to Crossroads");
			INFO.setDisplayName("technician_manual");
			INFO.setColor(Color.CYAN);
			INFO.setCategoryList(categories);
			INFO.setRegistryName(new ResourceLocation(Main.MODID, "info_guide"));
			INFO.setSpawnWithBook(false);
			return INFO;
		}

		@Override
		public void handleModel(ItemStack bookStack){
			GuideAPI.setModel(INFO);
		}

		@Override
		public void handlePost(ItemStack bookStack){
			GameRegistry.addShapelessRecipe(bookStack, GuideAPI.getStackFromBook(GuideBooks.MAIN));
		}
	}
	/**
	 * Splits up a long string into pages. I can't use PageHelper for this
	 * because of the § symbol.
	 */
	private static void createTextPages(ArrayList<IPage> pages, String text){

		final int PERPAGE = smallText ? 370 : 260;
		final char symbol = 167;
		String format = "";
		String formatTemp = "";

		int start = 0;
		double length = 0;
		for(int i = 0; i < text.length(); i++){
			if(text.charAt(i) == symbol){
				formatTemp = text.substring(i, i + 4);
				i += 4;
			}else if(i == text.length() - 1 || (length >= PERPAGE && text.charAt(i) == ' ')){
				//The .replace is to fix a bug where somehow (no clue how) some of the § symbols get turned to character 157. This turns them back.
				pages.add(new PageText((format + text.substring(start, i + 1)).replace((char) 157, symbol)));
				if(smallText){
					((Page) pages.get(pages.size() - 1)).setUnicodeFlag(true);
				}
				format = formatTemp;
				length = 0;
				start = i + 1;
			}else{
				//Bold text is thicker than normal text.
				length += formatTemp.equals("§r§l") ? 1.34D : 1;
			}
		}
	}

	private static void createPages(ArrayList<IPage> pages, Object... parts){
		for(Object obj : parts){
			if(obj instanceof String){
				//By default, pages localize by themselves. However, it is necessary to localize them on initialization for page splitting to work properly,
				//because the built in method doesn't support § and I have to make my own. This means reloading lang files in-game WILL NOT WORK for the guide
				//Also, the lang files need to be encoded in UTF-8 to support §.
				createTextPages(pages, TextHelper.localize((String) obj));
			}else if(obj instanceof Pair && ((Pair<?, ?>) obj).getLeft() instanceof String && ((Pair<?, ?>) obj).getRight() instanceof Object[]){
				@SuppressWarnings("unchecked")
				Pair<String, Object[]> pair = ((Pair<String, Object[]>) obj);
				createTextPages(pages, TextHelper.localize(pair.getLeft(), pair.getRight()));
			}else if(obj instanceof ItemStack){
				pages.add(new PageFurnaceRecipe((ItemStack) obj));
			}else if(obj instanceof ResourceLocation){
				pages.add(new PageImage((ResourceLocation) obj));
			}else if(obj instanceof IRecipe){
				pages.add(new PageIRecipe((IRecipe) obj));
			}else{
				throw new IllegalArgumentException("INVALID OBJECT FOR PAGE BUILDING!");
			}
		}
	}
}

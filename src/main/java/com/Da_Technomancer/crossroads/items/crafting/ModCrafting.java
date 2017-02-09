package com.Da_Technomancer.crossroads.items.crafting;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.enums.HeatConductors;
import com.Da_Technomancer.crossroads.API.enums.HeatInsulators;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.fluids.BlockDistilledWater;
import com.Da_Technomancer.crossroads.fluids.BlockMoltenCopper;
import com.Da_Technomancer.crossroads.integration.GuideAPI.GuideBooks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.itemSets.HeatCableFactory;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetUp;

import amerifrance.guideapi.api.GuideAPI;
import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public final class ModCrafting{

	public static void initCrafting(){

		OreDictionary.registerOre("wool", new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE));

		RecipeHolder.grindRecipes.put(new OreDictCraftingStack("oreCopper", 1), new ItemStack[] {new ItemStack(Item.getByNameOrId(Main.MODID + ":dustCopper"), 2), new ItemStack(Blocks.SAND)});
		RecipeHolder.grindRecipes.put(new CraftingStack(Items.WHEAT, 1, 0), new ItemStack[] {new ItemStack(Items.WHEAT_SEEDS, 3)});
		RecipeHolder.grindRecipes.put(new CraftingStack(Blocks.PUMPKIN, 1, 0), new ItemStack[] {new ItemStack(Items.PUMPKIN_SEEDS, 8)});
		RecipeHolder.grindRecipes.put(new CraftingStack(Items.MELON, 1, 0), new ItemStack[] {new ItemStack(Items.MELON_SEEDS, 3)});
		RecipeHolder.grindRecipes.put(new CraftingStack(Items.BONE, 1, 0), new ItemStack[] {new ItemStack(Items.DYE, 5, EnumDyeColor.WHITE.getDyeDamage())});
		RecipeHolder.grindRecipes.put(new OreDictCraftingStack("blockCoal", 1), new ItemStack[] {new ItemStack(Items.GUNPOWDER, 1)});
		RecipeHolder.grindRecipes.put(new CraftingStack(Blocks.NETHER_WART_BLOCK, 1, 0), new ItemStack[] {new ItemStack(Items.NETHER_WART, 9)});
		RecipeHolder.grindRecipes.put(new OreDictCraftingStack("cropPotato", 1), new ItemStack[] {new ItemStack(ModItems.mashedPotato, 1)});
		RecipeHolder.grindRecipes.put(new OreDictCraftingStack("gravel", 1), new ItemStack[] {new ItemStack(Items.FLINT)});
		RecipeHolder.grindRecipes.put(new OreDictCraftingStack("blockRedstone", 1), new ItemStack[] {new ItemStack(Items.REDSTONE, 9)});
		RecipeHolder.grindRecipes.put(new OreDictCraftingStack("cobblestone", 1), new ItemStack[] {new ItemStack(Blocks.SAND, 1)});
		
		// Heating, order of decreasing effectiveness
		RecipeHolder.envirHeatSource.put(Blocks.LAVA, Triple.of(Blocks.COBBLESTONE.getDefaultState(), 1000D, 3000D));
		RecipeHolder.envirHeatSource.put(Blocks.MAGMA, Triple.of(Blocks.NETHERRACK.getDefaultState(), 500D, 2000D));
		RecipeHolder.envirHeatSource.put(Blocks.FIRE, Triple.of(null, 300D, 2000D));
		// Cooling, order of increasing effectiveness
		RecipeHolder.envirHeatSource.put(Blocks.SNOW, Triple.of(Blocks.WATER.getDefaultState(), -50D, -20D));
		RecipeHolder.envirHeatSource.put(Blocks.ICE, Triple.of(Blocks.WATER.getDefaultState(), -70D, -50D));
		RecipeHolder.envirHeatSource.put(Blocks.PACKED_ICE, Triple.of(Blocks.WATER.getDefaultState(), -140D, -100D));

		RecipeHolder.fluidCoolingRecipes.put(BlockMoltenCopper.getMoltenCopper(), Pair.of(144, Triple.of(new ItemStack(OreSetUp.ingotCopper, 1), 1000D, 100D)));
		RecipeHolder.fluidCoolingRecipes.put(FluidRegistry.LAVA, Pair.of(1000, Triple.of(new ItemStack(Blocks.OBSIDIAN, 1), 1000D, 500D)));
		RecipeHolder.fluidCoolingRecipes.put(BlockDistilledWater.getDistilledWater(), Pair.of(1000, Triple.of(new ItemStack(Blocks.PACKED_ICE, 1), -20D, 2D)));
		RecipeHolder.fluidCoolingRecipes.put(FluidRegistry.WATER, Pair.of(1000, Triple.of(new ItemStack(Blocks.ICE, 1), -10D, 1D)));

		RecipeHolder.poisonBoboRecipes.add(Pair.of(new ICraftingStack[] {new CraftingStack(Blocks.HOPPER, 1, 0), new OreDictCraftingStack("wool", 1), new CraftingStack(ModBlocks.fluidTube, 1, 0)}, getFilledHopper()));
		RecipeHolder.poisonBoboRecipes.add(Pair.of(new ICraftingStack[] {new CraftingStack(Items.BREAD, 1, 0), new OreDictCraftingStack("dyeMagenta", 1), new OreDictCraftingStack("dustGlowstone", 1)}, new ItemStack(ModItems.magentaBread)));
		if(ModConfig.weatherControl.getBoolean()){
			RecipeHolder.poisonBoboRecipes.add(Pair.of(new ICraftingStack[] {new OreDictCraftingStack("gemLapis", 1), new OreDictCraftingStack("cobblestone", 1), new OreDictCraftingStack("nuggetGold", 1)}, new ItemStack(ModItems.rainIdol, 1)));
		}
		RecipeHolder.poisonBoboRecipes.add(Pair.of(new ICraftingStack[] {new OreDictCraftingStack("feather", 1), new OreDictCraftingStack("leather", 1), new CraftingStack(Blocks.WATERLILY, 1, 0)}, new ItemStack(ModItems.chickenBoots, 1)));
		RecipeHolder.poisonBoboRecipes.add(Pair.of(new ICraftingStack[] {new CraftingStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage()), new CraftingStack(Items.FISH, 1, 3), new OreDictCraftingStack("leather", 1)}, new ItemStack(ModItems.squidHelmet, 1)));
		RecipeHolder.poisonBoboRecipes.add(Pair.of(new ICraftingStack[] {new CraftingStack(Items.BLAZE_POWDER, 1, 0), new OreDictCraftingStack("leather", 1), new CraftingStack(Items.PORKCHOP, 1, 0)}, new ItemStack(ModItems.pigZombieChestplate, 1)));
		RecipeHolder.poisonBoboRecipes.add(Pair.of(new ICraftingStack[] {new CraftingStack(Items.MILK_BUCKET, 1, 0), new OreDictCraftingStack("leather", 1), new CraftingStack(Items.BEEF, 1, 0)}, new ItemStack(ModItems.cowLeggings, 1)));
		RecipeHolder.poisonBoboRecipes.add(Pair.of(new ICraftingStack[] {new CraftingStack(Items.BLAZE_ROD, 1, 0), new CraftingStack(Items.DRAGON_BREATH, 1, 0), new CraftingStack(Items.GOLDEN_APPLE, 1, -1)}, new ItemStack(ModItems.chaosRod, 1)));
		
		RecipeHolder.magExtractRecipes.put(Items.REDSTONE, new MagicUnit(24, 36, 0, 0));
		RecipeHolder.magExtractRecipes.put(ModItems.dustSalt, new MagicUnit(0, 24, 36, 0));
		RecipeHolder.magExtractRecipes.put(Items.COAL, new MagicUnit(36, 24, 0, 0));
		RecipeHolder.magExtractRecipes.put(Items.GLOWSTONE_DUST, new MagicUnit(1, 1, 1, 0));

		if(Loader.isModLoaded("guideapi")){
			// Guide book
			GameRegistry.addShapelessRecipe(GuideAPI.getStackFromBook(GuideBooks.MAIN), Items.BOOK, Items.COMPASS);
			GameRegistry.addShapelessRecipe(GuideAPI.getStackFromBook(GuideBooks.MAIN), GuideAPI.getStackFromBook(GuideBooks.INFO));
			GameRegistry.addShapelessRecipe(GuideAPI.getStackFromBook(GuideBooks.INFO), GuideAPI.getStackFromBook(GuideBooks.MAIN));
		}

		// Axle
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.axle, 1), "#", "?", "#", '#', Blocks.STONE, '?', "ingotIron"));
		// Bronze
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(OreSetUp.ingotBronze, 1), "###", "#?#", "###", '#', "nuggetCopper", '?', "nuggetTin"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(OreSetUp.blockBronze, 1), "###", "#?#", "###", '#', "ingotCopper", '?', "ingotTin"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(OreSetUp.blockBronze, 9), "###", "#?#", "###", '#', "blockCopper", '?', "blockTin"));
		// Pipe
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fluidTube, 8), "###", "   ", "###", '#', "ingotBronze"));
		// Hand Crank
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.handCrank, 1), " ?", "##", "$ ", '?', Blocks.LEVER, '#', "stickWood", '$', "cobblestone"));
		// Master Axis
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.masterAxis, 1), "###", "# #", "#$#", '#', "ingotIron", '$', "stickIron"));
		// Heating Crucible
		GameRegistry.addRecipe(new ItemStack(ModBlocks.heatingCrucible, 1), "# #", "#?#", "###", '#', Blocks.HARDENED_CLAY, '?', Items.CAULDRON);
		// Grindstone
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.grindstone, 1), "#$#", "#?#", "#$#", '#', "cobblestone", '?', "stickIron", '$', Blocks.PISTON));
		// Heat Cable
		for(HeatInsulators insul : HeatInsulators.values()){
			for(HeatConductors cond : HeatConductors.values()){
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(HeatCableFactory.HEAT_CABLES.get(cond).get(insul), 4), "###", "???", "###", '#', insul.getItem(), '?', cond.getItem()));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(HeatCableFactory.REDSTONE_HEAT_CABLES.get(cond).get(insul), 1), "###", "#?#", "###", '#', "dustRedstone", '?', HeatCableFactory.HEAT_CABLES.get(cond).get(insul)));
			}
		}
		// Steam Boiler
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.steamBoiler, 1), "###", "# #", "&&&", '#', "ingotBronze", '&', "ingotCopper"));
		// Salt Block
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.blockSalt, 1), "##", "##", '#', "dustSalt"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.dustSalt, 4), "#", '#', "blockSalt"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.blockSaltTile, 4), "##", "##", '#', "blockSalt"));
		// Rotary Pump
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.rotaryPump, 1), "#$#", "#$#", "&$&", '#', "ingotBronze", '&', "blockGlass", '$', "stickIron"));
		GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.rotaryPump, 1), ModBlocks.steamTurbine);
		// Steam Turbine
		GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.steamTurbine, 1), ModBlocks.rotaryPump);
		// Brazier
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.brazier, 1), "###", " $ ", " $ ", '$', "stoneAndesitePolished", '#', "stoneAndesite"));
		// Obsidian Cutting Kit
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.obsidianKit, 4), " # ", "#$#", " # ", '$', "obsidian", '#', Items.FLINT));
		// Thermometer
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.thermometer, 1), "#", "$", "?", '#', "dyeRed", '$', "stickIron", '?', "blockGlass"));
		// Fluid Gauge
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.fluidGauge, 1), " * ", "*#*", " *$", '#', "blockGlass", '*', "ingotIron", '$', ModBlocks.fluidTube));
		// Speedometer
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.speedometer, 1), "#", "$", '#', "string", '$', Items.COMPASS));
		// OmniMeter
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.omnimeter, 1), " # ", "&$%", " ? ", '#', ModItems.fluidGauge, '&', ModItems.thermometer, '$', "gemEmerald", '%', ModItems.speedometer, '?', Items.CLOCK));
		// Fluid Tank (second recipe is for clearing contents)
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fluidTank, 1), " $ ", "$#$", " $ ", '#', "ingotGold", '$', "ingotBronze"));
		GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.fluidTank, 1), ModBlocks.fluidTank);
		// Heat Exchanger
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.heatExchanger, 1), "#$#", "$$$", "###", '#', Blocks.IRON_BARS, '$', "ingotCopper"));
		// Insulated Heat Exchanger
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.insulHeatExchanger, 1), "###", "#$#", "###", '#', "obsidian", '$', ModBlocks.heatExchanger));
		// Coal Heater
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.coalHeater, 1), "#*#", "# #", "###", '#', "cobblestone", '*', "ingotCopper"));
		// Heating Chamber
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.heatingChamber, 1), "#*#", "# #", "###", '#', "ingotIron", '*', "ingotCopper"));
		// Salt Reactor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.saltReactor, 1), "#$#", "$%$", "#@#", '#', "ingotTin", '$', ModBlocks.fluidTube, '%', "blockSalt", '@', "ingotCopper"));
		// Fluid Cooling Chamber
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fluidCoolingChamber, 1), "###", "# #", "%%%", '#', "ingotTin", '%', "ingotIron"));
		// Slotted Chest
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.slottedChest, 1), "###", "$@$", "###", '#', "slabWood", '$', Blocks.TRAPDOOR, '@', "chestWood"));
		// Sorting Hopper
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.sortingHopper, 1), "# #", "#&#", " # ", '#', "ingotCopper", '&', "chestWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.sortingHopper, 1), "#&#", "###", '#', "ingotCopper", '&', "chestWood"));
		// Candle Lilypad
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModBlocks.candleLilyPad), Blocks.WATERLILY, "torch"));
		// Item Chute
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.itemChute, 4), "#$#", "#$#", "#$#", '#', "ingotIron", '$', "stickIron"));
		// Item Chute Port
		GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.itemChutePort, 1), ModBlocks.itemChute, Blocks.IRON_TRAPDOOR);
		// Radiator
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.radiator, 1), "#$#", "#$#", "#$#", '#', ModBlocks.fluidTube, '$', "ingotIron"));
		// Rotary Drill
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.rotaryDrill, 2), " * ", "*#*", '*', "ingotIron", '#', "blockIron"));
		// Fat Collector
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fatCollector, 1), "***", "# #", "*&*", '*', "ingotBronze", '#', "netherrack", '&', "ingotCopper"));
		// Fat Congealer
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fatCongealer, 1), "*^*", "# #", "* *", '*', "ingotBronze", '#', "netherrack", '^', "stickIron"));
		//Diamond wire
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.diamondWire, 3), "*&*", '*', "ingotTin", '&', "gemDiamond"));
		//Redstone Fluid Tube
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.redstoneFluidTube, 1), "***", "*&*", "***", '*', "dustRedstone", '&', ModBlocks.fluidTube));
		//Water Centrifuge
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.waterCentrifuge, 1), "*&*", "^%^", "* *", '*', "ingotBronze", '&', "stickIron", '^', ModBlocks.fluidTube, '%', "ingotTin"));
		//Pure Quartz
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.pureQuartz, 1), "dustSalt", "dustSalt", "gemQuartz"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.pureQuartz, 4), ModBlocks.blockPureQuartz));
		//Pure Quartz Block
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.blockPureQuartz, 1), "**", "**", '*', ModItems.pureQuartz));
		//Lens array
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.lensArray, 2), "*&*", "@#$", "***", '*', ModItems.pureQuartz, '&', "gemEmerald", '@', "gemRuby", '$', "gemDiamond"));
		//Arcane Extractor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.arcaneExtractor, 1), "***", "*# ", "***", '*', "stone", '#', ModItems.lensArray));
		//Small Quartz Stabilizer
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.smallQuartzStabilizer, 1), " * ", "*&*", "***", '*', ModItems.pureQuartz, '&', ModItems.lensArray));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.smallQuartzStabilizer, 1), " & ", "***", '&', ModItems.luminescentQuartz, '*', ModItems.pureQuartz));
		//Large Quartz Stabilizer
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.largeQuartzStabilizer, 1), "***", "*&*", "***", '*', ModItems.pureQuartz, '&', ModBlocks.smallQuartzStabilizer));
		//Crystalline Prism
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.crystallinePrism, 1), "*^*", "^&^", "*&*", '*', ModItems.pureQuartz, '^', ModItems.luminescentQuartz, '&', ModItems.lensArray));
		//Arcane Reflector
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.arcaneReflector, 1), "*^*", "^^^", "*^*", '*', "stone", '^', ModItems.pureQuartz));
		//Lens Holder
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.lensHolder, 1), "***", "*&*", "***", '*', "stone", '&', ModItems.pureQuartz));
		//Basic Beam Splitter
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.beamSplitterBasic, 1), "*^*", "*&*", "*^*", '*', ModItems.pureQuartz, '^', ModItems.luminescentQuartz, '&', ModItems.lensArray));
		//Redstone Beam Splitter
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModBlocks.beamSplitter, 1), ModBlocks.beamSplitterBasic, "dustRedstone", "dustRedstone", "dustRedstone"));
		//Color Chart
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.colorChart, 1), "RGB", "^^^", "___", '_', "slabWood", '^', "paper", 'R', "dyeRed", 'G', "dyeLime", 'B', "dyeBlue"));
		//Fertile Soil
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fertileSoil, 3, 0), "#$#", "***", "^^^", '#', new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', "cropWheat"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fertileSoil, 3, 1), "#$#", "***", "^^^", '#', new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', "cropPotato"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fertileSoil, 3, 2), "#$#", "***", "^^^", '#', new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', "cropCarrot"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fertileSoil, 3, 3), "#$#", "***", "^^^", '#', new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', Items.BEETROOT));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fertileSoil, 3, 4), "#$#", "***", "^^^", '#', new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.OAK.getMetadata())));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fertileSoil, 3, 5), "#$#", "***", "^^^", '#', new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.BIRCH.getMetadata())));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fertileSoil, 3, 6), "#$#", "***", "^^^", '#', new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.SPRUCE.getMetadata())));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fertileSoil, 3, 7), "#$#", "***", "^^^", '#', new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.JUNGLE.getMetadata())));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fertileSoil, 3, 8), "#$#", "***", "^^^", '#', new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.ACACIA.getMetadata())));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fertileSoil, 3, 9), "#$#", "***", "^^^", '#', new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.DARK_OAK.getMetadata())));
		//Piston
		GameRegistry.addRecipe(new ShapelessOreRecipe(Blocks.PISTON, "cobblestone", "ingotIron", "dustRedstone", "logWood"));
		//Multi-Piston
		GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.multiPiston, "***", "$#$", "$$$", '*', "ingotTin", '$', "ingotBronze", '#', Blocks.PISTON));
		//Sticky Multi-Piston
		GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.multiPistonSticky, "***", "$#$", "$$$", '*', "ingotTin", '$', "ingotBronze", '#', Blocks.STICKY_PISTON));
		GameRegistry.addRecipe(new ShapelessOreRecipe(ModBlocks.multiPistonSticky, ModBlocks.multiPiston, "slimeball"));
		//Crystalline Master Axis
		GameRegistry.addRecipe(new ItemStack(ModBlocks.crystalMasterAxis, 1), "*&*", "*#*", "***", '*', ModItems.pureQuartz, '#', ModBlocks.masterAxis, '&', ModItems.lensArray);
		//Void Crystal
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.voidCrystal, 1), "*#*", "###", "*#*", '*', Items.DRAGON_BREATH, '#', ModItems.pureQuartz));
		//Ratiator
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.ratiator, 1), " * ", "*#*", "^^^", '*', ModItems.luminescentQuartz, '#', ModItems.pureQuartz, '^', "stone"));
		//Beacon Harness
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.beaconHarness, 1), "*&*", "&^&", "*&*", '*', ModItems.pureQuartz, '&', ModItems.lensArray, '^', ModItems.luminescentQuartz));
		//Fat Feeder
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fatFeeder, 1), "*^*", "#&#", "*^*", '*', "ingotBronze", '#', "netherrack", '^', "stickIron", '&', "ingotTin"));
	}

	private static ItemStack getFilledHopper(){
		ItemStack stack = new ItemStack(Blocks.HOPPER);

		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList nbttag = new NBTTagList();
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		nbttagcompound.setByte("Slot", (byte) 0);
		new ItemStack(ModItems.vacuum).writeToNBT(nbttagcompound);
		nbttag.appendTag(nbttagcompound);
		nbt.setTag("Items", nbttag);
		stack.setTagInfo("BlockEntityTag", nbt);
		
		NBTTagCompound nbttagcompound1 = new NBTTagCompound();
		NBTTagList nbttaglist = new NBTTagList();
		nbttaglist.appendTag(new NBTTagString("(+NBT)"));
		nbttagcompound1.setTag("Lore", nbttaglist);
		stack.setTagInfo("display", nbttagcompound1);
		stack.setStackDisplayName("Vacuum Hopper");
		return stack;
	}
}

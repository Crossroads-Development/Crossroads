package com.Da_Technomancer.crossroads.items.crafting;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.GuideBooks;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.enums.HeatConductors;
import com.Da_Technomancer.crossroads.API.enums.HeatInsulators;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.fluids.BlockDistilledWater;
import com.Da_Technomancer.crossroads.fluids.BlockMoltenCopper;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.itemSets.HeatCableFactory;

import amerifrance.guideapi.api.GuideAPI;
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

		RecipeHolder.grindRecipes.put("oreCopper", new ItemStack[] {new ItemStack(Item.getByNameOrId(Main.MODID + ":dustCopper"), 2), new ItemStack(Blocks.SAND)});
		RecipeHolder.grindRecipes.put("minecraft:wheat", new ItemStack[] {new ItemStack(Items.WHEAT_SEEDS, 3)});
		RecipeHolder.grindRecipes.put("minecraft:pumpkin", new ItemStack[] {new ItemStack(Items.PUMPKIN_SEEDS, 8)});
		RecipeHolder.grindRecipes.put("minecraft:melon", new ItemStack[] {new ItemStack(Items.MELON_SEEDS, 3)});
		RecipeHolder.grindRecipes.put("minecraft:bone", new ItemStack[] {new ItemStack(Items.DYE, 5, EnumDyeColor.WHITE.getDyeDamage())});
		RecipeHolder.grindRecipes.put("blockCoal", new ItemStack[] {new ItemStack(Items.GUNPOWDER, 1)});
		RecipeHolder.grindRecipes.put("minecraft:nether_wart_block", new ItemStack[] {new ItemStack(Items.NETHER_WART, 9)});
		RecipeHolder.grindRecipes.put("cropPotato", new ItemStack[] {new ItemStack(ModItems.mashedPotato, 1)});
		RecipeHolder.grindRecipes.put("gravel", new ItemStack[] {new ItemStack(Items.FLINT)});

		// Heating, order of decreasing effectiveness
		RecipeHolder.envirHeatSource.put(Blocks.LAVA, Triple.of(Blocks.COBBLESTONE.getDefaultState(), 1000D, 3000D));
		RecipeHolder.envirHeatSource.put(Blocks.MAGMA, Triple.of(Blocks.NETHERRACK.getDefaultState(), 500D, 2000D));
		RecipeHolder.envirHeatSource.put(Blocks.FIRE, Triple.of(null, 300D, 2000D));
		// Cooling, order of increasing effectiveness
		RecipeHolder.envirHeatSource.put(Blocks.SNOW, Triple.of(Blocks.WATER.getDefaultState(), -50D, -20D));
		RecipeHolder.envirHeatSource.put(Blocks.ICE, Triple.of(Blocks.WATER.getDefaultState(), -70D, -50D));
		RecipeHolder.envirHeatSource.put(Blocks.PACKED_ICE, Triple.of(Blocks.WATER.getDefaultState(), -140D, -100D));

		RecipeHolder.fluidCoolingRecipes.put(BlockMoltenCopper.getMoltenCopper(), Pair.of(200, Triple.of(new ItemStack(Item.getByNameOrId(Main.MODID + ":ingotCopper"), 1), 1000D, 100D)));
		RecipeHolder.fluidCoolingRecipes.put(FluidRegistry.LAVA, Pair.of(1000, Triple.of(new ItemStack(Blocks.OBSIDIAN, 1), 1000D, 500D)));
		RecipeHolder.fluidCoolingRecipes.put(BlockDistilledWater.getDistilledWater(), Pair.of(1000, Triple.of(new ItemStack(Blocks.PACKED_ICE), -20D, 2D)));
		RecipeHolder.fluidCoolingRecipes.put(FluidRegistry.WATER, Pair.of(1000, Triple.of(new ItemStack(Blocks.ICE), -10D, 1D)));

		RecipeHolder.mashedBoboRecipes.add(Pair.of(new CraftingStack[] {new CraftingStack(Blocks.HOPPER, 1, 0), new OreDictCraftingStack("wool", 1), new CraftingStack(ModBlocks.fluidTube, 1, 0)}, getFilledHopper()));
		RecipeHolder.mashedBoboRecipes.add(Pair.of(new CraftingStack[] {new CraftingStack(Items.BREAD, 1, 0), new OreDictCraftingStack("dyeMagenta", 1), new OreDictCraftingStack("dustGlowstone", 1)}, new ItemStack(ModItems.magentaBread)));
		RecipeHolder.poisonBoboRecipes.add(Pair.of(new CraftingStack[] {new OreDictCraftingStack("feather", 1), new OreDictCraftingStack("leather", 1), new CraftingStack(Blocks.WATERLILY, 1, 0)}, new ItemStack(ModItems.chickenBoots, 1)));
		RecipeHolder.poisonBoboRecipes.add(Pair.of(new CraftingStack[] {new OreDictCraftingStack("gemLapis", 1), new OreDictCraftingStack("cobblestone", 1), new OreDictCraftingStack("nuggetGold", 1)}, new ItemStack(ModItems.rainIdol, 1)));
		
		final String axle = "stickIron";

		if(Loader.isModLoaded("guideapi")){
			// Guide book
			GameRegistry.addShapelessRecipe(GuideAPI.getStackFromBook(GuideBooks.main), Items.BOOK, Items.COMPASS);
		}

		// Axle
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.axle, 1), "#", "?", "#", '#', Blocks.STONE, '?', "ingotIron"));
		GameRegistry.addRecipe(new ItemStack(ModItems.axle, 1), "#", "?", "#", '#', Blocks.STONE, '?', ModItems.metalScrap);
		// Bronze
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getByNameOrId(Main.MODID + ":ingotBronze"), 1), "###", "#?#", "###", '#', "nuggetCopper", '?', "nuggetTin"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getByNameOrId(Main.MODID + ":blockBronze"), 1), "###", "#?#", "###", '#', "ingotCopper", '?', "ingotTin"));
		// Pipe
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fluidTube, 8), "###", "   ", "###", '#', "ingotBronze"));
		// Hand Crank
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.handCrank, 1), " ?", "##", "$ ", '?', Blocks.LEVER, '#', "stickWood", '$', "cobblestone"));
		// Master Axis
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.masterAxis, 1), "###", "#?#", "#$#", '#', "ingotIron", '?', Blocks.DROPPER, '$', axle));
		// Heating Crucible
		GameRegistry.addRecipe(new ItemStack(ModBlocks.heatingCrucible, 1), "# #", "#?#", "###", '#', Blocks.HARDENED_CLAY, '?', Items.CAULDRON);
		// Grindstone
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.grindstone, 1), "#$#", "#?#", "#$#", '#', "cobblestone", '?', axle, '$', Blocks.PISTON));
		// Heat Cable
		for(HeatInsulators insul : HeatInsulators.values()){
			for(HeatConductors cond : HeatConductors.values()){
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(HeatCableFactory.cableMap.get(cond).get(insul), 4), "###", "???", "###", '#', insul.getItem(), '?', cond.getItem()));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(HeatCableFactory.rCableMap.get(cond).get(insul), 1), "###", "#?#", "###", '#', "dustRedstone", '?', HeatCableFactory.cableMap.get(cond).get(insul)));
			}
		}
		// Steam Boiler
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.steamBoiler, 1), "###", "#?#", "&&&", '#', "ingotBronze", '?', "blockBronze", '&', "ingotCopper"));
		// Salt Block
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.blockSalt, 1), "##", "##", '#', "dustSalt"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.dustSalt, 4), "#", '#', "blockSalt"));
		// Rotary Pump
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.rotaryPump, 1), "#$#", "#$#", "&$&", '#', "ingotBronze", '&', "blockGlass", '$', axle));
		GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.rotaryPump, 1), ModBlocks.steamTurbine);
		// Steam Turbine
		GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.steamTurbine, 1), ModBlocks.rotaryPump);
		// Brazier
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.brazier, 1), "###", " $ ", " $ ", '$', "stoneAndesitePolished", '#', "stoneAndesite"));
		// Obsidian cutting kit
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.obsidianKit, 4), " # ", "#$#", " # ", '$', "obsidian", '#', Items.FLINT));
		// Thermometer
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.thermometer, 1), "#", "$", "?", '#', "dyeRed", '$', axle, '?', "blockGlass"));
		// Fluid Gauge
		GameRegistry.addRecipe(new ItemStack(ModItems.fluidGauge, 1), "#", "$", '#', Items.COMPASS, '$', ModBlocks.fluidTube);
		// Speedometer
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.speedometer, 1), " #", "#$", '#', "string", '$', "ingotIron"));
		// OmniMeter
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.omnimeter, 1), " # ", "&$%", " ? ", '#', ModItems.fluidGauge, '&', ModItems.thermometer, '$', "gemEmerald", '%', ModItems.speedometer, '?', Items.CLOCK));
		// Fluid Tank
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fluidTank, 1), " $ ", "$#$", " $ ", '#', "ingotGold", '$', "ingotBronze"));
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
		// Candle Lilypad
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModBlocks.candleLilyPad), Blocks.WATERLILY, "torch"));
		// Item Chute
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.itemChute, 4), "#$#", "#$#", "#$#", '#', "ingotIron", '$', axle));
		// Item Chute Port
		GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.itemChutePort, 1), ModBlocks.itemChute, Blocks.IRON_TRAPDOOR);
		// Radiator
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.radiator, 1), "#$#", "#$#", "#$#", '#', ModBlocks.fluidTube, '$', "ingotIron"));
		// Rotary Drill
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.rotaryDrill, 2), " * ", "*#*", '*', "ingotIron", '#', "blockIron"));
		// Fat Collector
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fatCollector, 1), "***", "# #", "*&*", '*', "ingotBronze", '#', "netherrack", '&', "ingotCopper"));
		// Fat Congealer
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fatCongealer, 1), "*^*", "# #", "* *", '*', "ingotBronze", '#', "netherrack", '^', axle));
		//Diamond wire
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.diamondWire, 3), "*&*", '*', "ingotTin", '&', "gemDiamond"));
		//Redstone Fluid Tube
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.redstoneFluidTube, 1), "***", "*&*", "***", '*', "dustRedstone", '&', ModBlocks.fluidTube));
		//Water Centrifuge
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.waterCentrifuge, 1), "*&*", "^%^", "* *", '*', "ingotBronze", '&', "stickIron", '^', ModBlocks.fluidTube, '%', "ingotTin"));
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

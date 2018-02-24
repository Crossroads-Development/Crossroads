package com.Da_Technomancer.crossroads.items.crafting;

import java.util.ArrayList;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.rotary.GearTypes;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.fluids.BlockDistilledWater;
import com.Da_Technomancer.crossroads.fluids.BlockMoltenCopper;
import com.Da_Technomancer.crossroads.fluids.BlockMoltenCopshowium;
import com.Da_Technomancer.crossroads.fluids.BlockMoltenGold;
import com.Da_Technomancer.crossroads.fluids.BlockMoltenIron;
import com.Da_Technomancer.crossroads.fluids.BlockMoltenTin;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.items.itemSets.HeatCableFactory;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.BlockStone;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public final class ModCrafting{

	public static final ArrayList<IRecipe> toRegister = new ArrayList<IRecipe>();
	/**
	 * The Object should either be a Block, Item, or ItemStack. The String[] contains keys to register it under. 
	 */
	public static final ArrayList<Pair<Object, String[]>> toRegisterOreDict = new ArrayList<Pair<Object, String[]>>();

	@SuppressWarnings("unchecked")
	public static void init(){

		toRegisterOreDict.add(Pair.of(new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE), new String[] {"wool"}));

		for(Pair<Object, String[]> oreDictMapping : toRegisterOreDict){
			Object left = oreDictMapping.getLeft();
			if(left instanceof Block){
				for(String key : oreDictMapping.getRight()){
					OreDictionary.registerOre(key, (Block) oreDictMapping.getLeft());
				}
			}else if(left instanceof Item){
				for(String key : oreDictMapping.getRight()){
					OreDictionary.registerOre(key, (Item) oreDictMapping.getLeft());
				}
			}else if(left instanceof ItemStack){
				for(String key : oreDictMapping.getRight()){
					OreDictionary.registerOre(key, (ItemStack) oreDictMapping.getLeft());
				}
			}else{
				throw Main.logger.throwing(new ClassCastException("INVALID object in toRegisterOreDict: " + left + "; Must be Block, Item, or ItemStack."));
			}

		}
		toRegisterOreDict.clear();

		RecipeHolder.grindRecipes.put(new OreDictCraftingStack("oreCopper", 1), new ItemStack[] {new ItemStack(ModItems.dustCopper, 2), new ItemStack(Blocks.SAND)});
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
		RecipeHolder.grindRecipes.put(new OreDictCraftingStack("ingotCopper", 1), new ItemStack[] {new ItemStack(ModItems.dustCopper, 1)});
		RecipeHolder.grindRecipes.put(new OreDictCraftingStack("stone", 1),  new ItemStack[] {new ItemStack(Blocks.GRAVEL, 1)});

		RecipeHolder.heatingCrucibleRecipes.add(Triple.of(new OreDictCraftingStack("ingotIron", 1), new FluidStack(BlockMoltenIron.getMoltenIron(), 144), "minecraft:blocks/iron_block"));
		RecipeHolder.heatingCrucibleRecipes.add(Triple.of(new OreDictCraftingStack("ingotGold", 1), new FluidStack(BlockMoltenGold.getMoltenGold(), 144), "minecraft:blocks/gold_block"));
		RecipeHolder.heatingCrucibleRecipes.add(Triple.of(new OreDictCraftingStack("ingotTin", 1), new FluidStack(BlockMoltenTin.getMoltenTin(), 144), Main.MODID + ":blocks/block_tin"));
		RecipeHolder.heatingCrucibleRecipes.add(Triple.of(new OreDictCraftingStack("ingotCopper", 1), new FluidStack(BlockMoltenCopper.getMoltenCopper(), 144), Main.MODID + ":blocks/block_copper"));
		RecipeHolder.heatingCrucibleRecipes.add(Triple.of(new OreDictCraftingStack("dustCopper", 1), new FluidStack(BlockMoltenCopper.getMoltenCopper(), 144), Main.MODID + ":blocks/ore_native_copper"));
		RecipeHolder.heatingCrucibleRecipes.add(Triple.of(new OreDictCraftingStack("oreCopper", 1), new FluidStack(BlockMoltenCopper.getMoltenCopper(), 144), Main.MODID + ":blocks/ore_copper"));
		RecipeHolder.heatingCrucibleRecipes.add(Triple.of(new OreDictCraftingStack("cobblestone", 1), new FluidStack(FluidRegistry.LAVA, 200), "minecraft:blocks/cobblestone"));

		// Heating, order of decreasing effectiveness
		RecipeHolder.envirHeatSource.put(Blocks.LAVA, Pair.of(true, Triple.of(Blocks.COBBLESTONE.getDefaultState(), 1000D, 3000D)));
		RecipeHolder.envirHeatSource.put(Blocks.FLOWING_LAVA, Pair.of(false, Triple.of(Blocks.COBBLESTONE.getDefaultState(), 1000D, 3000D)));
		RecipeHolder.envirHeatSource.put(Blocks.MAGMA, Pair.of(true, Triple.of(Blocks.NETHERRACK.getDefaultState(), 500D, 2000D)));
		RecipeHolder.envirHeatSource.put(Blocks.FIRE, Pair.of(true, Triple.of(Blocks.AIR.getDefaultState(), 300D, 2000D)));
		// Cooling, order of increasing effectiveness
		RecipeHolder.envirHeatSource.put(Blocks.SNOW, Pair.of(true, Triple.of(Blocks.WATER.getDefaultState(), -50D, -20D)));
		RecipeHolder.envirHeatSource.put(Blocks.ICE, Pair.of(true, Triple.of(Blocks.WATER.getDefaultState(), -70D, -50D)));
		RecipeHolder.envirHeatSource.put(Blocks.PACKED_ICE, Pair.of(true, Triple.of(Blocks.WATER.getDefaultState(), -140D, -100D)));

		RecipeHolder.fluidCoolingRecipes.put(BlockMoltenIron.getMoltenIron(), Pair.of(144, Triple.of(new ItemStack(Items.IRON_INGOT, 1), 1500D, 100D)));
		RecipeHolder.fluidCoolingRecipes.put(BlockMoltenGold.getMoltenGold(), Pair.of(144, Triple.of(new ItemStack(Items.GOLD_INGOT, 1), 1000D, 100D)));
		RecipeHolder.fluidCoolingRecipes.put(BlockMoltenTin.getMoltenTin(), Pair.of(144, Triple.of(new ItemStack(OreSetup.ingotTin, 1), 200D, 100D)));
		RecipeHolder.fluidCoolingRecipes.put(BlockMoltenCopper.getMoltenCopper(), Pair.of(144, Triple.of(new ItemStack(OreSetup.ingotCopper, 1), 1000D, 100D)));
		RecipeHolder.fluidCoolingRecipes.put(FluidRegistry.LAVA, Pair.of(1000, Triple.of(new ItemStack(Blocks.OBSIDIAN, 1), 1000D, 500D)));
		RecipeHolder.fluidCoolingRecipes.put(BlockDistilledWater.getDistilledWater(), Pair.of(1000, Triple.of(new ItemStack(Blocks.PACKED_ICE, 1), -20D, 2D)));
		RecipeHolder.fluidCoolingRecipes.put(FluidRegistry.WATER, Pair.of(1000, Triple.of(new ItemStack(Blocks.ICE, 1), -10D, 1D)));
		RecipeHolder.fluidCoolingRecipes.put(BlockMoltenCopshowium.getMoltenCopshowium(), Pair.of(144, Triple.of(new ItemStack(OreSetup.ingotCopshowium, 1), 1000D, 100D)));

		RecipeHolder.brazierBoboRecipes.add(Pair.of(new ICraftingStack[] {new CraftingStack(Blocks.HOPPER, 1, 0), new OreDictCraftingStack("wool", 1), new CraftingStack(ModBlocks.fluidTube, 1, 0)}, getFilledHopper()));
		RecipeHolder.brazierBoboRecipes.add(Pair.of(new ICraftingStack[] {new CraftingStack(Items.BREAD, 1, 0), new OreDictCraftingStack("dyeMagenta", 1), new OreDictCraftingStack("dustGlowstone", 1)}, new ItemStack(ModItems.magentaBread)));
		if(ModConfig.weatherControl.getBoolean()){
			RecipeHolder.brazierBoboRecipes.add(Pair.of(new ICraftingStack[] {new OreDictCraftingStack("gemLapis", 1), new OreDictCraftingStack("cobblestone", 1), new OreDictCraftingStack("nuggetGold", 1)}, new ItemStack(ModItems.rainIdol, 1)));
		}
		RecipeHolder.brazierBoboRecipes.add(Pair.of(new ICraftingStack[] {new OreDictCraftingStack("feather", 1), new OreDictCraftingStack("leather", 1), new CraftingStack(Blocks.WATERLILY, 1, 0)}, new ItemStack(ModItems.chickenBoots, 1)));
		RecipeHolder.brazierBoboRecipes.add(Pair.of(new ICraftingStack[] {new CraftingStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage()), new CraftingStack(Items.FISH, 1, 3), new OreDictCraftingStack("leather", 1)}, new ItemStack(ModItems.squidHelmet, 1)));
		RecipeHolder.brazierBoboRecipes.add(Pair.of(new ICraftingStack[] {new CraftingStack(Items.BLAZE_POWDER, 1, 0), new OreDictCraftingStack("leather", 1), new CraftingStack(Items.PORKCHOP, 1, 0)}, new ItemStack(ModItems.pigZombieChestplate, 1)));
		RecipeHolder.brazierBoboRecipes.add(Pair.of(new ICraftingStack[] {new CraftingStack(Items.MILK_BUCKET, 1, 0), new OreDictCraftingStack("leather", 1), new CraftingStack(Items.BEEF, 1, 0)}, new ItemStack(ModItems.cowLeggings, 1)));
		RecipeHolder.brazierBoboRecipes.add(Pair.of(new ICraftingStack[] {new CraftingStack(Items.BLAZE_ROD, 1, 0), new CraftingStack(Items.DRAGON_BREATH, 1, 0), new CraftingStack(Items.GOLDEN_APPLE, 1, -1)}, new ItemStack(ModItems.chaosRod, 1)));
		RecipeHolder.brazierBoboRecipes.add(Pair.of(new ICraftingStack[] {new CraftingStack(Blocks.SPONGE, 1, 0), new CraftingStack(ModBlocks.fluidTube, 1, 0), new CraftingStack(ModItems.voidCrystal, 1, 0)}, new ItemStack(ModBlocks.fluidVoid, 1)));
		RecipeHolder.brazierBoboRecipes.add(Pair.of(new ICraftingStack[] {new EdibleBlobCraftingStack(4, 2, 1), new OreDictCraftingStack("stickIron", 1), new OreDictCraftingStack("nuggetCopshowium", 1)}, new ItemStack(ModBlocks.hamsterWheel, 1)));

		RecipeHolder.magExtractRecipes.put(Items.REDSTONE, new MagicUnit(24, 36, 0, 0));
		RecipeHolder.magExtractRecipes.put(ModItems.dustSalt, new MagicUnit(0, 24, 36, 0));
		RecipeHolder.magExtractRecipes.put(Items.COAL, new MagicUnit(36, 24, 0, 0));
		RecipeHolder.magExtractRecipes.put(Items.GLOWSTONE_DUST, new MagicUnit(1, 1, 1, 0));
		RecipeHolder.magExtractRecipes.put(ModItems.sulfur, new MagicUnit(60, 0, 0, 0));
		RecipeHolder.magExtractRecipes.put(ModItems.solidQuicksilver, new MagicUnit(0, 60, 0, 0));
		RecipeHolder.magExtractRecipes.put(ModItems.wasteSalt, new MagicUnit(0, 0, 60, 0));

		//Fusion beam
		RecipeHolder.fusionBeamRecipes.put(new BlockCraftingStack(Blocks.SNOW.getDefaultState(), false), new BeamTransmute(Blocks.ICE.getDefaultState(), 1));
		RecipeHolder.fusionBeamRecipes.put(new BlockCraftingStack(Blocks.SAND.getDefaultState(), false), new BeamTransmute(ModBlocks.blockPureQuartz.getDefaultState(), 16));
		RecipeHolder.fusionBeamRecipes.put(new BlockCraftingStack(Blocks.NETHERRACK.getDefaultState(), false), new BeamTransmute(Blocks.NETHER_BRICK.getDefaultState(), 8));
		RecipeHolder.fusionBeamRecipes.put(new BlockCraftingStack(Blocks.GRAVEL.getDefaultState(), false), new BeamTransmute(Blocks.PRISMARINE.getDefaultState(), 8));
		RecipeHolder.fusionBeamRecipes.put(new BlockCraftingStack(Blocks.COBBLESTONE.getDefaultState(), false), new BeamTransmute(Blocks.STONE.getDefaultState(), 1));
		RecipeHolder.fusionBeamRecipes.put(new BlockCraftingStack(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.STONE), false), new BeamTransmute(Blocks.STONEBRICK.getDefaultState(), 1));
		RecipeHolder.fusionBeamRecipes.put(new BlockCraftingStack(Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.ROUGH), false), new BeamTransmute(Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.BRICKS), 12));
		RecipeHolder.fusionBeamRecipes.put(new BlockCraftingStack(Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.BRICKS), false), new BeamTransmute(Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.DARK), 16));
		//Void fusion beam
		RecipeHolder.vFusionBeamRecipes.put(new BlockCraftingStack(Blocks.ICE.getDefaultState(), false), new BeamTransmute(Blocks.SNOW.getDefaultState(), 0));
		RecipeHolder.vFusionBeamRecipes.put(new BlockCraftingStack(ModBlocks.blockPureQuartz.getDefaultState(), false), new BeamTransmute(Blocks.SAND.getDefaultState(), 16));
		RecipeHolder.vFusionBeamRecipes.put(new BlockCraftingStack(Blocks.STONE.getDefaultState(), false), new BeamTransmute(Blocks.COBBLESTONE.getDefaultState(), 1));
		RecipeHolder.vFusionBeamRecipes.put(new BlockCraftingStack(Blocks.STONEBRICK.getDefaultState(), false), new BeamTransmute(Blocks.STONE.getDefaultState(), 1));
		RecipeHolder.vFusionBeamRecipes.put(new BlockCraftingStack(Blocks.NETHER_BRICK.getDefaultState(), false), new BeamTransmute(Blocks.NETHERRACK.getDefaultState(), 8));
		RecipeHolder.vFusionBeamRecipes.put(new BlockCraftingStack(Blocks.PRISMARINE.getDefaultState(), false), new BeamTransmute(Blocks.GRAVEL.getDefaultState(), 8));
		RecipeHolder.vFusionBeamRecipes.put(new BlockCraftingStack(Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.BRICKS), false), new BeamTransmute(Blocks.PRISMARINE.getDefaultState(), 12));
		RecipeHolder.vFusionBeamRecipes.put(new BlockCraftingStack(Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.DARK), false), new BeamTransmute(Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.BRICKS), 16));

		//Custom tool 
		//(sword)
		RecipeHolder.alchemyRecipes.add(new CustomToolRecipe("sword", new byte[][] {{1, 0, 0}, {1, 0, 0}, {2, 0, 0}}));
		RecipeHolder.alchemyRecipes.add(new CustomToolRecipe("sword", new byte[][] {{0, 1, 0}, {0, 1, 0}, {0, 2, 0}}));
		RecipeHolder.alchemyRecipes.add(new CustomToolRecipe("sword", new byte[][] {{0, 0, 1}, {0, 0, 1}, {0, 0, 2}}));
		//(pickaxe)
		RecipeHolder.alchemyRecipes.add(new CustomToolRecipe("pickaxe", new byte[][] {{1, 1, 1}, {0, 2, 0}, {0, 2, 0}}));
		//(shovel)
		RecipeHolder.alchemyRecipes.add(new CustomToolRecipe("shovel", new byte[][] {{1, 0, 0}, {2, 0, 0}, {2, 0, 0}}));
		RecipeHolder.alchemyRecipes.add(new CustomToolRecipe("shovel", new byte[][] {{0, 1, 0}, {0, 2, 0}, {0, 2, 0}}));
		RecipeHolder.alchemyRecipes.add(new CustomToolRecipe("shovel", new byte[][] {{0, 0, 1}, {0, 0, 2}, {0, 0, 2}}));
		//(axe)
		RecipeHolder.alchemyRecipes.add(new CustomToolRecipe("axe", new byte[][] {{1, 1, 0}, {1, 2, 0}, {0, 2, 0}}));
		RecipeHolder.alchemyRecipes.add(new CustomToolRecipe("axe", new byte[][] {{0, 1, 1}, {0, 2, 1}, {0, 2, 0}}));
		RecipeHolder.alchemyRecipes.add(new CustomToolRecipe("axe", new byte[][] {{1, 1, 0}, {2, 1, 0}, {2, 0, 0}}));
		RecipeHolder.alchemyRecipes.add(new CustomToolRecipe("axe", new byte[][] {{0, 1, 1}, {0, 1, 2}, {0, 0, 2}}));
		
		//Copshowium Creation Chamber
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.copshowiumCreationChamber, 1), "*^*", "^&^", "*^*", '*', ModItems.pureQuartz, '^', ModItems.luminescentQuartz, '&', ModBlocks.fluidCoolingChamber));
		//Chunk Unlocker
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.chunkUnlocker, 1), "*^*", "^&^", "*^*", '*', "ingotBronze", '^', "ingotCopshowium", '&', ModItems.lensArray));
		//Gateway Frame
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.gatewayFrame, 3), "***", "^^^", "%^%", '*', Blocks.STONE, '^', "ingotCopshowium", '%', "obsidian"));
		//Mechanical Beam Splitter
		RecipeHolder.technomancyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.mechanicalBeamSplitter, 1), ModBlocks.beamSplitter, "ingotCopshowium", "ingotCopshowium", "stickIron"));
		//Beam Cage
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.beamCage, 1), "*&*", '*', ModBlocks.largeQuartzStabilizer, '&', "ingotCopshowium"));
		//Cage Charger
		RecipeHolder.technomancyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.cageCharger, 1), "ingotBronze", "ingotBronze", "ingotCopshowium", ModItems.pureQuartz));
		//Beam Staff
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.staffTechnomancy, 1), "*&*", " & ", " | ", '*', ModItems.lensArray, '&', "ingotCopshowium", '|', "stickIron"));
		//Modular Goggles
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.moduleGoggles, 1), "***", "^&^", '&', "ingotCopshowium", '*', "ingotBronze", '^', "blockGlass"));
		//Prototype Port
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.prototypePort, 1), "*&*", "& &", "*&*", '*', "ingotBronze", '&', "nuggetCopshowium"));
		//Prototyping Table
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.prototypingTable, 1), "*&*", "&%&", "*&*", '*', "ingotBronze", '&', "ingotCopshowium", '%', ModBlocks.detailedCrafter));
		//Redstone Axis
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.redstoneAxis, 1), "*^*", "^&^", "*^*", '*', "dustRedstone", '^', "nuggetBronze", '&', ModBlocks.masterAxis));
		//Multiplication Axis
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.multiplicationAxis, 1), "***", "%^&", "***", '*', "nuggetBronze", '%', "gearCopshowium", '^', "wool", '&', "stickIron"));
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.multiplicationAxis, 1), "***", "%^&", "***", '*', "nuggetBronze", '%', "gearCopshowium", '^', "leather", '&', "stickIron"));
		//Addition Axis
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.additionAxis, 1), "***", "&^&", "***", '*', "nuggetBronze", '&', "stickIron", '^', "gearCopshowium"));
		//Equals Axis
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.equalsAxis, 1), "***", " & ", "***", '*', "nuggetBronze", '&', ModBlocks.masterAxis));
		//Greater Than Axis
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.greaterThanAxis, 1), false, "** ", " &*", "** ", '*', "nuggetBronze", '&', ModBlocks.masterAxis));
		//Less Than Axis
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.lessThanAxis, 1), false, " **", "*& ", " **", '*', "nuggetBronze", '&', ModBlocks.masterAxis));
		//Square Root Axis
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.squareRootAxis, 1), " **", "*& ", " * ", '*', "nuggetBronze", '&', ModBlocks.masterAxis));
		//Sin Axis
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.sinAxis, 1), " **", " & ", "** ", '*', "nuggetBronze", '&', ModBlocks.masterAxis));
		//Cos Axis
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.cosAxis, 1), " * ", "*&*", "* *", '*', "nuggetBronze", '&', ModBlocks.masterAxis));
		//ArcSin Axis
		RecipeHolder.technomancyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.arcsinAxis, 1), ModBlocks.sinAxis));
		//ArcCos Axis
		RecipeHolder.technomancyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.arccosAxis, 1), ModBlocks.cosAxis));
		//Flux Regulated Axis
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fluxReaderAxis, 1), "***", "*&*", "***", '*', "nuggetCopshowium", '&', ModBlocks.masterAxis));
		//Master Axis (Cheap Technomancy recipe)
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.masterAxis, 1), "***", "*#*", "*&*", '*', "nuggetIron", '#', "nuggetCopshowium", '&', "stickIron"));
		//Rate Manipulator
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.rateManipulator, 2), "*^*", "^&^", "*^*", '*', "ingotBronze", '^', "ingotCopshowium", '&', "gemEmerald"));
		//Flux Manipulator
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fluxManipulator, 2), "*^*", "^&^", "*^*", '*', "ingotBronze", '^', "ingotCopshowium", '&', "gemRuby"));
		//Pistol
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.pistol, 1), "CBB", "CA ", 'C', "ingotCopshowium", 'B', "ingotBronze", 'A', ModItems.lensArray));
		toRegister.add(new PrototypeItemSetRecipe(ModItems.pistol, "prot"));
		toRegister.add(new PrototypeItemClearRecipe(ModItems.pistol, "prot"));
		//Watch
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.watch, 1), " * ", "*&*", " * ", '*', "ingotBronze", '&', "ingotCopshowium"));
		toRegister.add(new PrototypeItemSetRecipe(ModItems.watch, "prot"));
		toRegister.add(new PrototypeItemClearRecipe(ModItems.watch, "prot"));
		//Mechanical Arm
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.mechanicalArm, 1), " * ", " ||", "***", '|', "stickIron", '*', "gearCopshowium"));
		//Redstone Registry
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.redstoneRegistry, 1), "*&*", "&^&", "*&*", '*', "nuggetTin", '&', ModBlocks.redstoneKeyboard, '^', "ingotCopshowium"));
		//Detailed Crafting Table (Cheap Technomancy Recipe)
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.detailedCrafter, 1), "*&*", "&#&", "*&*", '*', "nuggetIron", '&', "nuggetTin", '#', Blocks.CRAFTING_TABLE));

		//Ores
		//Tin
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(OreSetup.nuggetTin, 9), "ingotTin"));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(OreSetup.ingotTin, 9), "blockTin"));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(OreSetup.ingotTin, 1), "***", "***", "***", '*', "nuggetTin"));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(OreSetup.blockTin, 1), "***", "***", "***", '*', "ingotTin"));
		GameRegistry.addSmelting(new ItemStack(OreSetup.oreTin, 1), new ItemStack(OreSetup.ingotTin, 1), .7F);

		//Copper
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(OreSetup.nuggetCopper, 9), "ingotCopper"));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(OreSetup.ingotCopper, 9), "blockCopper"));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(OreSetup.ingotCopper, 1), "***", "***", "***", '*', "nuggetCopper"));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(OreSetup.blockCopper, 1), "***", "***", "***", '*', "ingotCopper"));

		//Bronze
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(OreSetup.nuggetBronze, 9), "ingotBronze"));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(OreSetup.ingotBronze, 9), "blockBronze"));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(OreSetup.ingotBronze, 1), "***", "***", "***", '*', "nuggetBronze"));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(OreSetup.blockBronze, 1), "***", "***", "***", '*', "ingotBronze"));

		//Ruby
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(OreSetup.gemRuby, 4), "blockRuby"));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(OreSetup.blockRuby, 1), "**", "**", '*', "gemRuby"));
		GameRegistry.addSmelting(new ItemStack(OreSetup.oreRuby, 1), new ItemStack(OreSetup.gemRuby, 1), 1F);

		//Copshowium
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(OreSetup.nuggetCopshowium, 9), "ingotCopshowium"));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(OreSetup.ingotCopshowium, 9), "blockCopshowium"));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(OreSetup.ingotCopshowium, 1), "***", "***", "***", '*', "nuggetCopshowium"));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(OreSetup.blockCopshowium, 1), "***", "***", "***", '*', "ingotCopshowium"));

		// Axle
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.axle, 1), "#", "?", "#", '#', Blocks.STONE, '?', "ingotIron"));
		// Bronze
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(OreSetup.ingotBronze, 1), "###", "#?#", "###", '#', "nuggetCopper", '?', "nuggetTin"));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(OreSetup.blockBronze, 1), "###", "#?#", "###", '#', "ingotCopper", '?', "ingotTin"));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(OreSetup.blockBronze, 9), "###", "#?#", "###", '#', "blockCopper", '?', "blockTin"));
		// Pipe
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fluidTube, 8), "###", "   ", "###", '#', "ingotBronze"));
		// Hand Crank
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModItems.handCrank, 1), " ?", "##", "$ ", '?', Blocks.LEVER, '#', "stickWood", '$', "cobblestone"));
		// Master Axis
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.masterAxis, 1), "###", "# #", "#$#", '#', "ingotIron", '$', "stickIron"));
		// Heating Crucible
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.heatingCrucible, 1), "# #", "#?#", "###", '#', Blocks.HARDENED_CLAY, '?', Items.CAULDRON));
		// Grindstone
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.grindstone, 1), "#$#", "#?#", "#$#", '#', "cobblestone", '?', "stickIron", '$', Blocks.PISTON));
		// Heat Cable
		for(HeatInsulators insul : HeatInsulators.values()){
			toRegister.add(new ShapedOreRecipe(null, new ItemStack(HeatCableFactory.HEAT_CABLES.get(insul), 4), "###", "???", "###", '#', insul.getItem(), '?', "ingotCopper"));
			toRegister.add(new ShapedOreRecipe(null, new ItemStack(HeatCableFactory.REDSTONE_HEAT_CABLES.get(insul), 1), "###", "#?#", "###", '#', "dustRedstone", '?', HeatCableFactory.HEAT_CABLES.get(insul)));
		}
		//Toggle Gear
		for(GearTypes type : GearTypes.values()){
			toRegister.add(new ShapedOreRecipe(null, new ItemStack(GearFactory.BASIC_GEARS.get(type), 9), " ? ", "?#?", " ? ", '#', "block" + type.toString(), '?', "ingot" + type.toString()));
			toRegister.add(new ShapedOreRecipe(null, new ItemStack(GearFactory.BASIC_GEARS.get(type), 1), " ? ", "?#?", " ? ", '#', "ingot" + type.toString(), '?', "nugget" + type.toString()));
			toRegister.add(new ShapelessOreRecipe(null, GearFactory.TOGGLE_GEARS.get(type), "dustRedstone", "dustRedstone", "stickIron", GearFactory.BASIC_GEARS.get(type)));
			toRegister.add(new ShapedOreRecipe(null, new ItemStack(GearFactory.LARGE_GEARS.get(type), 1), "###", "#$#", "###", '#', GearFactory.BASIC_GEARS.get(type), '$', "block" + type.toString()));
		}
		// Steam Boiler
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.steamBoiler, 1), "###", "# #", "&&&", '#', "ingotBronze", '&', "ingotCopper"));
		// Salt Block
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.blockSalt, 1), "##", "##", '#', "dustSalt"));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModItems.dustSalt, 4), "#", '#', "blockSalt"));
		// Rotary Pump
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.rotaryPump, 1), "#$#", "#$#", "&$&", '#', "ingotBronze", '&', "blockGlass", '$', "stickIron"));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.rotaryPump, 1), ModBlocks.steamTurbine));
		// Steam Turbine
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.steamTurbine, 1), ModBlocks.rotaryPump));
		// Brazier
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.brazier, 1), "###", " $ ", " $ ", '$', "stoneAndesitePolished", '#', "stoneAndesite"));
		// Obsidian Cutting Kit
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModItems.obsidianKit, 4), " # ", "#$#", " # ", '$', "obsidian", '#', Items.FLINT));
		// Thermometer
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModItems.thermometer, 1), "#", "$", "?", '#', "dyeRed", '$', "stickIron", '?', "blockGlass"));
		// Fluid Gauge
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModItems.fluidGauge, 1), " * ", "*#*", " *$", '#', "blockGlass", '*', "ingotIron", '$', ModBlocks.fluidTube));
		// Speedometer
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModItems.speedometer, 1), "#", "$", '#', "string", '$', Items.COMPASS));
		// OmniMeter
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModItems.omnimeter, 1), " # ", "&$%", " ? ", '#', ModItems.fluidGauge, '&', ModItems.thermometer, '$', "ingotBronze", '%', ModItems.speedometer, '?', Items.CLOCK));
		// Fluid Tank (second recipe is for clearing contents)
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fluidTank, 1), " $ ", "$#$", " $ ", '#', "ingotGold", '$', "ingotBronze"));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.fluidTank, 1), ModBlocks.fluidTank));
		// Heat Exchanger
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.heatExchanger, 1), "#$#", "$$$", "###", '#', Blocks.IRON_BARS, '$', "ingotCopper"));
		// Insulated Heat Exchanger
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.insulHeatExchanger, 1), "###", "#$#", "###", '#', "obsidian", '$', ModBlocks.heatExchanger));
		// Coal Heater
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fuelHeater, 1), "#*#", "# #", "###", '#', "cobblestone", '*', "ingotCopper"));
		// Heating Chamber
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.heatingChamber, 1), "#*#", "# #", "###", '#', "ingotIron", '*', "ingotCopper"));
		// Salt Reactor
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.saltReactor, 1), "#$#", "$%$", "#@#", '#', "ingotIron", '$', ModBlocks.fluidTube, '%', "blockSalt", '@', "ingotCopper"));
		// Fluid Cooling Chamber
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fluidCoolingChamber, 1), "###", "# #", "#%#", '#', "ingotIron", '%', "ingotCopper"));
		// Slotted Chest
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.slottedChest, 1), "###", "$@$", "###", '#', "slabWood", '$', Blocks.TRAPDOOR, '@', "chestWood"));
		// Sorting Hopper
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.sortingHopper, 1), "# #", "#&#", " # ", '#', "ingotCopper", '&', "chestWood"));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.sortingHopper, 1), "#&#", "###", '#', "ingotCopper", '&', "chestWood"));
		// Candle Lilypad
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.candleLilyPad), Blocks.WATERLILY, "torch"));
		// Item Chute
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.itemChute, 4), "#$#", "#$#", "#$#", '#', "ingotIron", '$', "stickIron"));
		// Item Chute Port
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.itemChutePort, 1), ModBlocks.itemChute, Blocks.IRON_TRAPDOOR));
		// Radiator
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.radiator, 1), "#$#", "#$#", "#$#", '#', ModBlocks.fluidTube, '$', "ingotIron"));
		// Rotary Drill
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.rotaryDrill, 2), " * ", "*#*", '*', "ingotIron", '#', "blockIron"));
		// Fat Collector
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fatCollector, 1), "***", "# #", "*&*", '*', "ingotTin", '#', "netherrack", '&', "ingotCopper"));
		// Fat Congealer
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fatCongealer, 1), "*^*", "# #", "* *", '*', "ingotTin", '#', "netherrack", '^', "stickIron"));
		//Redstone Fluid Tube
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.redstoneFluidTube, 1), "***", "*&*", "***", '*', "dustRedstone", '&', ModBlocks.fluidTube));
		//Water Centrifuge
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.waterCentrifuge, 1), "*&*", "^%^", "* *", '*', "ingotBronze", '&', "stickIron", '^', ModBlocks.fluidTube, '%', "ingotTin"));
		//Pure Quartz
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModItems.pureQuartz, 1), "dustSalt", "dustSalt", "gemQuartz"));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModItems.pureQuartz, 4), ModBlocks.blockPureQuartz));
		//Pure Quartz Block
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.blockPureQuartz, 1), "**", "**", '*', ModItems.pureQuartz));
		//Lens array
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModItems.lensArray, 2), "*&*", "@ $", "***", '*', ModItems.pureQuartz, '&', "gemEmerald", '@', "gemRuby", '$', "gemDiamond"));
		//Arcane Extractor
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.arcaneExtractor, 1), "***", "*# ", "***", '*', "obsidian", '#', ModItems.lensArray));
		//Small Quartz Stabilizer
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.smallQuartzStabilizer, 1), " * ", "*&*", "***", '*', ModItems.pureQuartz, '&', ModItems.lensArray));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.smallQuartzStabilizer, 1), " & ", "***", '&', ModItems.luminescentQuartz, '*', ModItems.pureQuartz));
		//Large Quartz Stabilizer
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.largeQuartzStabilizer, 1), "***", "*&*", "***", '*', ModItems.pureQuartz, '&', ModBlocks.smallQuartzStabilizer));
		//Crystalline Prism
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.crystallinePrism, 1), "*^*", "^&^", "*&*", '*', ModItems.pureQuartz, '^', ModItems.luminescentQuartz, '&', ModItems.lensArray));
		//Arcane Reflector
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.arcaneReflector, 1), "*^*", '*', "stone", '^', ModItems.pureQuartz));
		//Lens Holder
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.lensHolder, 1), "***", "*&*", "***", '*', "stone", '&', ModItems.pureQuartz));
		//Basic Beam Splitter
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.beamSplitterBasic, 1), "*^*", "*&*", "*^*", '*', ModItems.pureQuartz, '^', ModItems.luminescentQuartz, '&', ModItems.lensArray));
		//Redstone Beam Splitter
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.beamSplitter, 1), ModBlocks.beamSplitterBasic, "dustRedstone", "dustRedstone", "dustRedstone"));
		//Color Chart
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.colorChart, 1), "RGB", "^^^", "___", '_', "slabWood", '^', "paper", 'R', "dyeRed", 'G', "dyeLime", 'B', "dyeBlue"));
		//Fertile Soil
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fertileSoil, 3, 0), "#$#", "***", "^^^", '#', new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', "cropWheat"));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fertileSoil, 3, 1), "#$#", "***", "^^^", '#', new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', "cropPotato"));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fertileSoil, 3, 2), "#$#", "***", "^^^", '#', new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', "cropCarrot"));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fertileSoil, 3, 3), "#$#", "***", "^^^", '#', new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', Items.BEETROOT));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fertileSoil, 3, 4), "#$#", "***", "^^^", '#', new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.OAK.getMetadata())));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fertileSoil, 3, 5), "#$#", "***", "^^^", '#', new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.BIRCH.getMetadata())));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fertileSoil, 3, 6), "#$#", "***", "^^^", '#', new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.SPRUCE.getMetadata())));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fertileSoil, 3, 7), "#$#", "***", "^^^", '#', new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.JUNGLE.getMetadata())));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fertileSoil, 3, 8), "#$#", "***", "^^^", '#', new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.ACACIA.getMetadata())));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fertileSoil, 3, 9), "#$#", "***", "^^^", '#', new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.DARK_OAK.getMetadata())));
		//Piston
		toRegister.add(new ShapelessOreRecipe(null, Blocks.PISTON, "cobblestone", "ingotIron", "dustRedstone", "logWood"));
		//Multi-Piston
		toRegister.add(new ShapedOreRecipe(null, ModBlocks.multiPiston, "***", "$#$", "$$$", '*', "ingotTin", '$', "ingotBronze", '#', Blocks.PISTON));
		//Sticky Multi-Piston
		toRegister.add(new ShapedOreRecipe(null, ModBlocks.multiPistonSticky, "***", "$#$", "$$$", '*', "ingotTin", '$', "ingotBronze", '#', Blocks.STICKY_PISTON));
		toRegister.add(new ShapelessOreRecipe(null, ModBlocks.multiPistonSticky, ModBlocks.multiPiston, "slimeball"));
		//Crystalline Master Axis
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.crystalMasterAxis, 1), "*&*", "*#*", "***", '*', ModItems.pureQuartz, '#', ModBlocks.masterAxis, '&', ModItems.lensArray));
		//Void Crystal
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModItems.voidCrystal, 1), "*#*", "###", "*#*", '*', Items.DRAGON_BREATH, '#', ModItems.pureQuartz));
		//Ratiator
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.ratiator, 1), " * ", "*#*", "^^^", '*', ModItems.luminescentQuartz, '#', ModItems.pureQuartz, '^', "stone"));
		//Beacon Harness
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.beaconHarness, 1), "*&*", "&^&", "*&*", '*', ModItems.pureQuartz, '&', ModItems.lensArray, '^', ModItems.luminescentQuartz));
		//Fat Feeder
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fatFeeder, 1), "*^*", "# #", "*^*", '*', "ingotTin", '#', "netherrack", '^', "stickIron"));
		//Detailed Crafter
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.detailedCrafter, 1), "*^*", "^&^", "*^*", '*', "ingotIron", '^', "ingotTin", '&', Blocks.CRAFTING_TABLE));
		//Basic Fluid Splitter
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.basicFluidSplitter, 1), "*^*", "&&&", "*^*", '*', "nuggetTin", '^', ModBlocks.fluidTube, '&', "ingotBronze"));
		//Redstone Fluid Splitter
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.fluidSplitter, 1), ModBlocks.basicFluidSplitter, "dustRedstone", "dustRedstone", "dustRedstone"));
		//Redstone Keyboard
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.redstoneKeyboard, 1), " & ", "&*&", " & ", '*', "ingotBronze", '&', "dustRedstone"));

		//Vanadium smelting
		GameRegistry.addSmelting(ModItems.vanadium, new ItemStack(ModItems.vanadiumVOxide, 1), .7F);
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

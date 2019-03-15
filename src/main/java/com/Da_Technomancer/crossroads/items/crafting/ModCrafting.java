package com.Da_Technomancer.crossroads.items.crafting;

import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.alchemy.EnumReagents;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.fluids.BlockDistilledWater;
import com.Da_Technomancer.crossroads.fluids.BlockMoltenCopshowium;
import com.Da_Technomancer.crossroads.fluids.BlockSteam;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.alchemy.Phial;
import com.Da_Technomancer.crossroads.items.itemSets.HeatCableFactory;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.EssentialsBlocks;
import com.Da_Technomancer.essentials.items.EssentialsItems;
import com.Da_Technomancer.essentials.items.crafting.EssentialsCrafting;
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
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.function.Predicate;

public final class ModCrafting{

	public static final ArrayList<IRecipe> toRegister = new ArrayList<>();
	/**
	 * The Object should either be a Block, Item, or ItemStack. The String[] contains keys to register it under. 
	 */
	public static final ArrayList<Pair<Object, String[]>> toRegisterOreDict = new ArrayList<>();

	public static void init(){

		RecipeHolder.millRecipes.put(new ItemRecipePredicate(Items.WHEAT, 0), new ItemStack[] {new ItemStack(Items.WHEAT_SEEDS, 3)});
		RecipeHolder.millRecipes.put(new ItemRecipePredicate(Blocks.PUMPKIN, 0), new ItemStack[] {new ItemStack(Items.PUMPKIN_SEEDS, 8)});
		RecipeHolder.millRecipes.put(new ItemRecipePredicate(Items.MELON, 0), new ItemStack[] {new ItemStack(Items.MELON_SEEDS, 3)});
		RecipeHolder.millRecipes.put(new ItemRecipePredicate(Items.BONE, 0), new ItemStack[] {new ItemStack(Items.DYE, 5, EnumDyeColor.WHITE.getDyeDamage())});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("blockCoal"), new ItemStack[] {new ItemStack(Items.GUNPOWDER, 1)});
		RecipeHolder.millRecipes.put(new ItemRecipePredicate(Blocks.NETHER_WART_BLOCK, 0), new ItemStack[] {new ItemStack(Items.NETHER_WART, 9)});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("cropPotato"), new ItemStack[] {new ItemStack(ModItems.mashedPotato, 1)});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("gravel"), new ItemStack[] {new ItemStack(Items.FLINT)});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("blockRedstone"), new ItemStack[] {new ItemStack(Items.REDSTONE, 9)});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("cobblestone"), new ItemStack[] {new ItemStack(Blocks.SAND, 1)});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("stone"),  new ItemStack[] {new ItemStack(Blocks.GRAVEL, 1)});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("oreQuartz"),  new ItemStack[] {new ItemStack(Items.QUARTZ, 2)});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("oreDiamond"),  new ItemStack[] {new ItemStack(Items.DIAMOND, 2)});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("oreCoal"),  new ItemStack[] {new ItemStack(Items.COAL, 2)});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("oreRedstone"),  new ItemStack[] {new ItemStack(Items.REDSTONE, 10)});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("oreLapis"),  new ItemStack[] {new ItemStack(Items.DYE, 10, EnumDyeColor.BLUE.getDyeDamage())});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("blockQuartz"),  new ItemStack[] {new ItemStack(Items.QUARTZ, 4)});

		RecipeHolder.dirtyWaterRecipes.add(Pair.of(5, new ItemStack(Items.GUNPOWDER)));
		RecipeHolder.dirtyWaterRecipes.add(Pair.of(5, new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage())));
		RecipeHolder.dirtyWaterRecipes.add(Pair.of(25, MiscUtil.getOredictStack("dustSalt", 1)));
		RecipeHolder.dirtyWaterRecipes.add(Pair.of(10, new ItemStack(Blocks.SAND)));
		RecipeHolder.dirtyWaterRecipes.add(Pair.of(5, new ItemStack(Blocks.DIRT)));
		RecipeHolder.dirtyWaterRecipes.add(Pair.of(1, new ItemStack(Items.REDSTONE)));

		for(Pair<Integer, ItemStack> ent : RecipeHolder.dirtyWaterRecipes){
			RecipeHolder.dirtyWaterWeights += ent.getLeft();
		}

		RecipeHolder.crucibleRecipes.put(new OreDictCraftingStack("cobblestone"), new FluidStack(FluidRegistry.LAVA, 200));
		RecipeHolder.crucibleRecipes.put(new OreDictCraftingStack("obsidian"), new FluidStack(FluidRegistry.LAVA, 1_000));

		RecipeHolder.coolingRecipes.put(new ItemRecipePredicate(Items.SNOWBALL, 0), 100);
		RecipeHolder.coolingRecipes.put(new ItemRecipePredicate(Blocks.SNOW, 0), 400);
		RecipeHolder.coolingRecipes.put(new ItemRecipePredicate(Blocks.ICE, 0), 1600);
		RecipeHolder.coolingRecipes.put(new ItemRecipePredicate(Blocks.PACKED_ICE, 0), 1600);

		RecipeHolder.fluidCoolingRecipes.put(FluidRegistry.LAVA, Pair.of(1000, Triple.of(new ItemStack(Blocks.OBSIDIAN, 1), 2500D, 1500D)));
		RecipeHolder.fluidCoolingRecipes.put(BlockSteam.getSteam(), Pair.of(1000, Triple.of(new ItemStack(Blocks.PACKED_ICE, 1), 0D, 15D + EnergyConverters.degPerSteamBucket(false))));
		RecipeHolder.fluidCoolingRecipes.put(BlockDistilledWater.getDistilledWater(), Pair.of(1000, Triple.of(new ItemStack(Blocks.PACKED_ICE, 1), 0D, 15D)));
		RecipeHolder.fluidCoolingRecipes.put(FluidRegistry.WATER, Pair.of(1000, Triple.of(new ItemStack(Blocks.ICE, 1), 0D, 15D)));
		RecipeHolder.fluidCoolingRecipes.put(BlockMoltenCopshowium.getMoltenCopshowium(), Pair.of(EnergyConverters.INGOT_MB, Triple.of(new ItemStack(OreSetup.ingotCopshowium, 1), 1500D, 100D)));

		if(ModConfig.addBoboRecipes.getBoolean()){
			registerBoboItem(getFilledHopper(), "Vacuum Hopper", new ItemRecipePredicate(Blocks.HOPPER, 0), new OreDictCraftingStack("wool"), new ItemRecipePredicate(ModBlocks.fluidTube, 0));
			registerBoboItem(ModItems.magentaBread, "Magenta Bread", new ItemRecipePredicate(Items.BREAD, 0), new OreDictCraftingStack("dyeMagenta"), new OreDictCraftingStack("dustGlowstone"));
			registerBoboItem(ModItems.rainIdol, "Rain Idol", new OreDictCraftingStack("gemLapis"), new OreDictCraftingStack("cobblestone"), new OreDictCraftingStack("nuggetGold"));
			registerBoboItem(ModItems.squidHelmet, "Squid Helmet", new ItemRecipePredicate(Items.DYE, EnumDyeColor.BLACK.getDyeDamage()), new ItemRecipePredicate(Items.FISH, 3), new OreDictCraftingStack("leather"));
			registerBoboItem(ModItems.pigZombieChestplate, "Zombie Pigman Chestplate", new ItemRecipePredicate(Items.BLAZE_POWDER, 0), new OreDictCraftingStack("leather"), new ItemRecipePredicate(Items.PORKCHOP, 0));
			registerBoboItem(ModItems.cowLeggings, "Cow Leggings", new ItemRecipePredicate(Items.MILK_BUCKET, 0), new OreDictCraftingStack("leather"), new ItemRecipePredicate(Items.BEEF, 0));
			registerBoboItem(ModItems.chickenBoots, "Chicken Boots", new OreDictCraftingStack("feather"), new OreDictCraftingStack("leather"), new ItemRecipePredicate(Blocks.WATERLILY, 0));
			registerBoboItem(ModItems.chaosRod, "Rod of Discord", new ItemRecipePredicate(Items.BLAZE_ROD, 0), new ItemRecipePredicate(Items.DRAGON_BREATH, 0), new ItemRecipePredicate(Items.GOLDEN_APPLE, -1));
			registerBoboItem(new ItemStack(ModBlocks.fluidVoid, 1), "Fluid Void", new ItemRecipePredicate(Blocks.SPONGE, 0), new ItemRecipePredicate(ModBlocks.fluidTube, 0), new ItemRecipePredicate(OreSetup.voidCrystal, 0));
			registerBoboItem(new ItemStack(ModBlocks.hamsterWheel, 1), "Hamster Wheel", new EdibleBlobRecipePredicate(4, 2), new ComponentCraftingStack("stick"), new OreDictCraftingStack("nuggetCopshowium"));
			registerBoboItem(ModItems.liechWrench, "Liechtensteinian Navy Wrench", (ItemStack s) -> EssentialsConfig.isWrench(s, false), new ItemRecipePredicate(ModItems.handCrank, 0), new ItemRecipePredicate(ModItems.staffTechnomancy, 0));
			registerBoboItem(new ItemStack(ModBlocks.maxwellDemon, 1), "Maxwell's Demon", new ItemRecipePredicate(Blocks.BEDROCK, 0), new EdibleBlobRecipePredicate(6, 4), new OreDictCraftingStack("ingotCopper"));
			registerBoboItem(new ItemStack(ModItems.nitroglycerin, 8), "Nitroglycerin", new OreDictCraftingStack("meatRaw"), new OreDictCraftingStack("gunpowder"), (Predicate<ItemStack>) (ItemStack stack) -> {
				if(stack.getItem() instanceof Phial){
					return ModItems.phialGlass.getReagants(stack).getQty(EnumReagents.NITRIC_ACID.id()) != 0;
				}
				return false;
			});
			registerBoboItem(new ItemStack(ModItems.poisonVodka, 1), "Poison Vodka", new ItemRecipePredicate(ModItems.solidVitriol, 0), new ItemRecipePredicate(Items.POISONOUS_POTATO, 0), (Predicate<ItemStack>) (ItemStack stack) -> stack.getItem() instanceof Phial || stack.getItem() == Items.GLASS_BOTTLE && stack.getCount() == 1);
			registerBoboItem(new ItemStack(ModItems.doublePoisonVodka, 1), "Double Poison Vodka", new ItemRecipePredicate(ModItems.solidVitriol, 0), new ItemRecipePredicate(Items.POISONOUS_POTATO, 0), new ItemRecipePredicate(ModItems.poisonVodka, 0));
		}



		RecipeHolder.beamExtractRecipes.put(Items.REDSTONE, new BeamUnit(18, 24, 0, 0));
		RecipeHolder.beamExtractRecipes.put(ModItems.dustSalt, new BeamUnit(0, 18, 24, 0));
		RecipeHolder.beamExtractRecipes.put(Items.COAL, new BeamUnit(24, 18, 0, 0));
		RecipeHolder.beamExtractRecipes.put(Items.GLOWSTONE_DUST, new BeamUnit(4, 4, 4, 0));
		RecipeHolder.beamExtractRecipes.put(ModItems.sulfur, new BeamUnit(32, 0, 0, 0));
		RecipeHolder.beamExtractRecipes.put(ModItems.solidQuicksilver, new BeamUnit(0, 32, 0, 0));
		RecipeHolder.beamExtractRecipes.put(ModItems.wasteSalt, new BeamUnit(0, 0, 32, 0));
		RecipeHolder.beamExtractRecipes.put(Items.ENDER_PEARL, new BeamUnit(32, 0, 32, 0));
		RecipeHolder.beamExtractRecipes.put(Items.DRAGON_BREATH, new BeamUnit(64, 0, 64, 0));
		RecipeHolder.beamExtractRecipes.put(Items.BLAZE_POWDER, new BeamUnit(32, 16, 0, 0));
		RecipeHolder.beamExtractRecipes.put(Items.GUNPOWDER, new BeamUnit(24, 0, 0, 0));
		RecipeHolder.beamExtractRecipes.put(Items.SLIME_BALL, new BeamUnit(0, 24, 0, 0));
		RecipeHolder.beamExtractRecipes.put(Items.SNOWBALL, new BeamUnit(0, 0, 24, 0));

		//Fusion beam
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.SNOW.getDefaultState(), false), new BeamTransmute(Blocks.ICE.getDefaultState(), 0));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.SAND.getDefaultState(), false), new BeamTransmute(ModBlocks.blockPureQuartz.getDefaultState(), 16));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.NETHERRACK.getDefaultState(), false), new BeamTransmute(Blocks.NETHER_BRICK.getDefaultState(), 8));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.COBBLESTONE.getDefaultState(), false), new BeamTransmute(Blocks.STONE.getDefaultState(), 0));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.DIRT.getDefaultState(), false), new BeamTransmute(Blocks.CLAY.getDefaultState(), 0));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.GRASS.getDefaultState(), true), new BeamTransmute(Blocks.CLAY.getDefaultState(), 0));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.STONE), false), new BeamTransmute(Blocks.STONEBRICK.getDefaultState(), 0));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.GRAVEL.getDefaultState(), false), new BeamTransmute(Blocks.PRISMARINE.getDefaultState(), 8));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.ROUGH), false), new BeamTransmute(Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.BRICKS), 16));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.BRICKS), false), new BeamTransmute(Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.DARK), 32));
		//Void fusion beam
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(Blocks.ICE.getDefaultState(), false), new BeamTransmute(Blocks.SNOW.getDefaultState(), 0));
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(ModBlocks.blockPureQuartz.getDefaultState(), false), new BeamTransmute(Blocks.SAND.getDefaultState(), 16));
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(Blocks.STONE.getDefaultState(), false), new BeamTransmute(Blocks.COBBLESTONE.getDefaultState(), 0));
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(Blocks.CLAY.getDefaultState(), false), new BeamTransmute(Blocks.DIRT.getDefaultState(), 0));
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(Blocks.STONEBRICK.getDefaultState(), false), new BeamTransmute(Blocks.STONE.getDefaultState(), 0));
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(Blocks.NETHER_BRICK.getDefaultState(), false), new BeamTransmute(Blocks.NETHERRACK.getDefaultState(), 8));
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(Blocks.PRISMARINE.getDefaultState(), false), new BeamTransmute(Blocks.GRAVEL.getDefaultState(), 8));
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.BRICKS), false), new BeamTransmute(Blocks.PRISMARINE.getDefaultState(), 16));
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.DARK), false), new BeamTransmute(Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.BRICKS), 32));

		//Phial
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.phialGlass, 1), "*", "*", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.phialCrystal, 1), "*", "*", '*', "gemAlcCryst"));
		//Florence Flask
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.florenceFlaskGlass, 1), " * ", "* *", "***", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.florenceFlaskCrystal, 1), " * ", "* *", "***", '*', "gemAlcCryst"));
		//Shell
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.shellGlass, 1), " * ", "* *", " * ", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.shellCrystal, 1), " * ", "* *", " * ", '*', "gemAlcCryst"));
		//Leyden Jar
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.leydenJar, 1), " | ", "*r*", "***", '|', "stickIron", 'r', "dustRedstone", '*', "nuggetIron"));
		//Alchemical Tube
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.alchemicalTubeGlass, 8), "***", "   ", "***", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.alchemicalTubeCrystal, 8), "***", "   ", "***", '*', "gemAlcCryst"));
		//Alembic
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.alembic, 1, 0), "** ", "***", "** ", '*', "ingotCopper"));
		//Chemical Vent
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.chemicalVent, 1, 0), "*#*", "###", "*#*", '*', "ingotTin", '#', Blocks.IRON_BARS));
		//Cooling Coil
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.coolingCoilGlass, 4), "* *", " * ", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.coolingCoilCrystal, 4), "* *", " * ", '*', "gemAlcCryst"));
		//Densus Plate
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.densusPlate, 6), "***", '*', "gemDensus"));
		//Anti-Densus plate
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.antiDensusPlate, 6), "***", '*', "gemAntiDensus"));
		//Flow Limiter
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.flowLimiterGlass, 2), "*:*", '*', "blockGlass", ':', "ingotGold"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.flowLimiterCrystal, 2), "*:*", '*', "gemAlcCryst", ':', "ingotGold"));
		//Fluid Injector
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fluidInjectorGlass, 1), "*|*", ": :", "*|*", '*', "ingotBronze", '|', ModBlocks.fluidTube, ':', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fluidInjectorCrystal, 1), "*|*", ": :", "*|*", '*', "ingotBronze", '|', ModBlocks.fluidTube, ':', "gemAlcCryst"));
		//Glassware Holder
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.glasswareHolder, 1, 0), "^^^", "^ ^", '^', "nuggetIron"));
		//Heated Tube
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.heatedTubeGlass, 2), "*#*", '*', "blockGlass", '#', "ingotCopper"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.heatedTubeCrystal, 2), "*#*", '*', "gemAlcCryst", '#', "ingotCopper"));
		//Heat Limiter
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.heatLimiterBasic, 4, 0), "*&*", "*&*", "*#*", '*', "obsidian", '#', "dustRedstone", '&', "ingotCopper"));
		RecipeHolder.alchemyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.heatLimiterRedstone, 1, 0), "dustRedstone", "dustRedstone", "dustRedstpme", ModBlocks.heatLimiterBasic));
		//Reaction Chamber
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.reactionChamberGlass, 1), "*^*", "^#^", "*^*", '*', "ingotBronze", '^', "blockGlass", '#', new ItemStack(ModBlocks.reagentTankGlass, 1)));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.reactionChamberCrystal, 1), "*^*", "^#^", "*^*", '*', "ingotBronze", '^', "gemAlcCryst", '#', new ItemStack(ModBlocks.reagentTankCrystal, 1)));
		//Reagent Pump
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.reagentPumpGlass, 1), "***", "*&*", "***", '&', "ingotBronze", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.reagentPumpCrystal, 1), "***", "*&*", "***", '&', "ingotBronze", '*', "gemAlcCryst"));
		//Reagent Tank
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.reagentTankGlass, 1), "*_*", "_ _", "*_*", '*', "ingotBronze", '_', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.reagentTankCrystal, 1), "*_*", "_ _", "*_*", '*', "ingotBronze", '_', "gemAlcCryst"));
		//Redstone Alchemical Tube
		RecipeHolder.alchemyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.redsAlchemicalTubeGlass, 1), "dustRedstone", "dustRedstone", "dustRedstone", new ItemStack(ModBlocks.alchemicalTubeGlass, 1)));
		RecipeHolder.alchemyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.redsAlchemicalTubeCrystal, 1), "dustRedstone", "dustRedstone", "dustRedstone", new ItemStack(ModBlocks.alchemicalTubeCrystal, 1)));
		//Tesla Coil
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.teslaCoil, 2), "|||", "^*^", "|||", '*', "ingotCopper", '|', "ingotIron", '^', "dustRedstone"));
		//Tesla Coil Tops
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.teslaCoilTopNormal, 2), "III", " C ", "RCR", 'C', "ingotCopper", 'I', "ingotIron", 'R', "dustRedstone"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.teslaCoilTopDistance, 1), "TTT", "TCT", "TTT", 'T', "ingotTin", 'C', ModBlocks.teslaCoilTopNormal));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.teslaCoilTopIntensity, 1), "TTT", "TCT", "TTT", 'T', "ingotGold", 'C', ModBlocks.teslaCoilTopNormal));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.teslaCoilTopAttack, 1), "TTT", "TCT", "TTT", 'T', "ingotCopper", 'C', ModBlocks.teslaCoilTopNormal));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.teslaCoilTopEfficiency, 1), "TTT", "TCT", "TTT", 'T', "ingotBronze", 'C', ModBlocks.teslaCoilTopNormal));
		//Vanadium
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.vanadiumOxide, 4), "***", "*B*", "***", '*', Items.COAL, 'B', "blockCoal"));
		//Charging Stand
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.chargingStand, 1), " * ", "| |", " ^ ", '*', "ingotIron", '|', "stickIron", '^', ModBlocks.glasswareHolder));
		//Atmos Charger
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.atmosCharger, 1), "| |", "| |", "*$*", '|', "stickIron", '*', "ingotIron", '$', ModItems.leydenJar));
		//Voltus Generator
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.voltusGenerator, 1), "*C*", "M$M", "*C*", 'M', "ingotCopper", 'C', ModItems.alchCrystal, '*', "ingotIron", '$', ModItems.leydenJar));
		//Detailed Crafting Table (Cheap Alchemy Recipe)
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.detailedCrafter, 1), "*&*", "&#&", "*&*", '*', "nuggetIron", '&', "nuggetTin", '#', Blocks.CRAFTING_TABLE));
		//Tesla Ray
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.teslaRay, 1), "C C", "VII", "C C", 'C', "ingotCopshowium", 'I', "ingotIron", 'V', ModItems.leydenJar));
		//Damping Powder
		RecipeHolder.alchemyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(ModItems.dampingPowder, 4), ModItems.wasteSalt, ModItems.wasteSalt, "dustSalt", "dustRedstone"));
		//Reagent Filter
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.reagentFilterGlass, 1), "III", "|A|", "III", 'I', "ingotIron", '|', "blockGlass", 'A', ModItems.lensArray));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.reagentFilterCrystal, 1), "III", "|A|", "III", 'I', "ingotIron", '|', ModItems.alchCrystal, 'A', ModItems.lensArray));

		//Flying Machine
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.flyingMachine, 1), "___", "@-@", "|+|", '_', "ingotBronze", '@', "gearCopshowium", '-', new ItemStack(ModBlocks.antiDensusPlate, 1), '+', new ItemStack(ModBlocks.densusPlate, 1), '|', "stickIron"));
		//Copshowium Creation Chamber
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.copshowiumCreationChamber, 1), "*^*", "^&^", "*^*", '*', ModItems.pureQuartz, '^', ModItems.luminescentQuartz, '&', ModBlocks.fluidCoolingChamber));
		//Gateway Frame
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.gatewayFrame, 3), "***", "^^^", "%^%", '*', Blocks.STONE, '^', "ingotCopshowium", '%', "obsidian"));
		//Beam Cage
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.beamCage, 1), " L ", "*&*", " L ", '*', ModBlocks.quartzStabilizer, '&', "ingotCopshowium", 'L', ModItems.lensArray));
		//Cage Charger
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.cageCharger, 1), " B ", "QLQ", 'B', "ingotBronze", 'Q', ModItems.pureQuartz, 'L', ModItems.luminescentQuartz));
		//Beam Staff
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.staffTechnomancy, 1), "*C*", " | ", " | ", '*', ModItems.lensArray, 'C', ModItems.beamCage, '|', "stickIron"));
		//Modular Goggles
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.moduleGoggles, 1), "***", "^&^", '&', "ingotCopshowium", '*', "ingotBronze", '^', "blockGlass"));
		//Prototype Port
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.prototypePort, 1), "*&*", "& &", "*&*", '*', "ingotBronze", '&', "nuggetCopshowium"));
		//Prototyping Table
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.prototypingTable, 1), "*&*", "&%&", "*&*", '*', "ingotBronze", '&', "ingotCopshowium", '%', ModBlocks.detailedCrafter));
		//Redstone Axis
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.redstoneAxis, 1), "*^*", "^&^", "*^*", '*', "dustRedstone", '^', "nuggetBronze", '&', ModBlocks.masterAxis));
		//Math Axis
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.mathAxis, 1), "B|B", "GAG", "B|B", 'B', "nuggetBronze", '|', "stickIron", 'G', "gearCopshowium", 'A', ModBlocks.masterAxis));
		//Master Axis (Cheap Technomancy recipe)
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.masterAxis, 1), "***", "*#*", "*&*", '*', "nuggetIron", '#', "nuggetCopshowium", '&', "stickIron"));
		//Pistol
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.pistol, 1), "CBB", "CA ", 'C', "ingotCopshowium", 'B', "ingotBronze", 'A', ModItems.lensArray));
		toRegister.add(new PrototypeItemSetRecipe(ModItems.pistol, "prot"));
		toRegister.add(new PrototypeItemClearRecipe(ModItems.pistol, "prot"));
		//Watch
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.watch, 1), " * ", "*&*", " * ", '*', "ingotBronze", '&', "ingotCopshowium"));
		toRegister.add(new PrototypeItemSetRecipe(ModItems.watch, "prot"));
		toRegister.add(new PrototypeItemClearRecipe(ModItems.watch, "prot"));
		//Mechanical Arm
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.mechanicalArm, 1), " *|", " | ", "*I*", 'I', "blockIron", '|', "stickIron", '*', "gearCopshowium"));
		//Redstone Registry
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.redstoneRegistry, 1), "*&*", "&^&", "*&*", '*', "nuggetTin", '&', ModBlocks.redstoneKeyboard, '^', "ingotCopshowium"));
		//Detailed Crafting Table (Cheap Technomancy Recipe)
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.detailedCrafter, 1), "*&*", "&#&", "*&*", '*', "nuggetIron", '&', "nuggetTin", '#', Blocks.CRAFTING_TABLE));
		//Clockwork Stabilizer
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.clockworkStabilizer, 1), " # ", "#*#", " # ", '*', ModBlocks.quartzStabilizer, '#', "gearCopshowium"));
		//Beacon Harness
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.beaconHarness, 1), "^&^", "&*&", "^&^", '*', "ingotCopshowium", '&', ModItems.lensArray, '^', ModItems.luminescentQuartz));
		//Chrono Harness
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.chronoHarness, 1), "^&^", "&*&", "^&^", '*', "ingotCopshowium", '&', "ingotIron", '^', "blockRedstone"));
		//Flux node
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fluxNode, 1), " | ", "|C|", "I|I", 'I', "ingotIron", '|', "stickIron", 'C', "ingotCopshowium"));
		//Temporal Accelerator
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.temporalAccelerator, 1), "CCC", "Q|Q", " | ", 'C', "ingotCopshowium", '|', "stickIron", 'Q', ModItems.luminescentQuartz));
		//Electric Flux Stabilizer
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fluxStabilizerElectric, 1), "RCR", "CGC", "RCR", 'C', "ingotCopshowium", 'G', "blockGlass", 'R', "dustRedstone"));
		//Mechanical Flux Stabilizer
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fluxStabilizerMechanical, 1), "RCR", "CGC", "RCR", 'C', "ingotCopshowium", 'G', "blockGlass", 'R', "gearIron"));
		//Beam Flux Stabilizer
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fluxStabilizerBeam, 1), "RCR", "CGC", "RCR", 'C', "ingotCopshowium", 'G', "blockGlass", 'R', ModItems.pureQuartz));
		//Electric Flux Crystal Stabilizer
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fluxStabilizerCrystalElectric, 1), "RCR", "CGC", "RCR", 'C', "ingotCopshowium", 'G', ModItems.alchCrystal, 'R', "dustRedstone"));
		//Mechanical Flux Crystal Stabilizer
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fluxStabilizerCrystalMechanical, 1), "RCR", "CGC", "RCR", 'C', "ingotCopshowium", 'G', ModItems.alchCrystal, 'R', "gearIron"));
		//Beam Flux Crystal Stabilizer
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fluxStabilizerCrystalBeam, 1), "RCR", "CGC", "RCR", 'C', "ingotCopshowium", 'G', ModItems.alchCrystal, 'R', ModItems.pureQuartz));

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
		GameRegistry.addSmelting(new ItemStack(OreSetup.oreCopper, 1), new ItemStack(OreSetup.ingotCopper, 1), .7F);

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

		// Bronze
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(OreSetup.nuggetBronze, 4), "nuggetCopper", "nuggetCopper", "nuggetCopper", "nuggetTin"));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(OreSetup.ingotBronze, 4), "ingotCopper", "ingotCopper", "ingotCopper", "ingotTin"));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(OreSetup.blockBronze, 4), "blockCopper", "blockCopper", "blockCopper", "blockTin"));
		// Pipe
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fluidTube, 8), "###", "   ", "###", '#', "ingotBronze"));
		// Hand Crank
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModItems.handCrank, 1), " ?", "##", "$ ", '?', Blocks.LEVER, '#', "stickWood", '$', "cobblestone"));
		// Master Axis
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.masterAxis, 1), "###", "# #", "#$#", '#', "ingotIron", '$', "stickIron"));
		// Heating Crucible
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.heatingCrucible, 1), "# #", "#?#", "###", '#', Blocks.HARDENED_CLAY, '?', Items.CAULDRON));
		// Millstone
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.millstone, 1), "#$#", "#?#", "#$#", '#', "cobblestone", '?', "stickIron", '$', Blocks.PISTON));
		// Heat Cable
		for(HeatInsulators insul : HeatInsulators.values()){
			toRegister.add(new ShapedOreRecipe(null, new ItemStack(HeatCableFactory.HEAT_CABLES.get(insul), 4), "###", "???", "###", '#', insul.getItem(), '?', "ingotCopper"));
			toRegister.add(new ShapelessOreRecipe(null, new ItemStack(HeatCableFactory.REDSTONE_HEAT_CABLES.get(insul), 1), "dustRedstone", "dustRedstone", "dustRedstone", HeatCableFactory.HEAT_CABLES.get(insul)));
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
		// OmniMeter
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModItems.omnimeter, 1), " * ", "*#*", " * ", '*', "ingotBronze", '#', Items.COMPASS));
		// Fluid Tank (second recipe is for clearing contents)
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fluidTank, 1), " $ ", "$#$", " $ ", '#', "ingotGold", '$', "ingotBronze"));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.fluidTank, 1), ModBlocks.fluidTank));
		// Heat Exchanger
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.heatSink, 1), "###", "#$#", "###", '#', Blocks.IRON_BARS, '$', "ingotCopper"));
		// Firebox
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.firebox, 1), "#*#", "#@#", "###", '#', "cobblestone", '*', "ingotCopper", '@', Blocks.FURNACE));
		// Icebox
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.icebox, 1), "#*#", "#@#", "###", '#', "cobblestone", '*', "ingotCopper", '@', "gemLapis"));
		// Smelter
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.smelter, 1), "#*#", "# #", "###", '#', "ingotIron", '*', "ingotCopper"));
		// Salt Reactor
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.saltReactor, 1), "#$#", "$%$", "#@#", '#', "ingotIron", '$', ModBlocks.fluidTube, '%', "blockSalt", '@', "ingotCopper"));
		// Fluid Cooling Chamber
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fluidCoolingChamber, 1), "###", "# #", "#%#", '#', "ingotIron", '%', "ingotCopper"));
		// Radiator
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.radiator, 1), "#$#", "#$#", "#$#", '#', ModBlocks.fluidTube, '$', "ingotIron"));
		// Rotary Drill
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.rotaryDrill, 2), " * ", "*#*", '*', "ingotIron", '#', "blockIron"));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.rotaryDrillGold, 1), " * ", "*#*", " * ", '*', "ingotGold", '#', ModBlocks.rotaryDrill));
		// Fat Collector
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fatCollector, 1), "***", "# #", "*&*", '*', "ingotTin", '#', "netherrack", '&', "ingotCopper"));
		// Fat Congealer
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fatCongealer, 1), "*^*", "# #", "* *", '*', "ingotTin", '#', "netherrack", '^', "stickIron"));
		//Redstone Fluid Tube
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.redstoneFluidTube, 1), "dustRedstone", "dustRedstone", "dustRedstone", ModBlocks.fluidTube));
		//Water Centrifuge
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.waterCentrifuge, 1), "*&*", "^%^", "* *", '*', "ingotBronze", '&', "stickIron", '^', ModBlocks.fluidTube, '%', "ingotTin"));
		//Pure Quartz
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModItems.pureQuartz, 1), "dustSalt", "dustSalt", "gemQuartz"));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModItems.pureQuartz, 4), ModBlocks.blockPureQuartz));
		//Pure Quartz Block
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.blockPureQuartz, 1), "**", "**", '*', ModItems.pureQuartz));
		//Luminescent Quartz
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModItems.luminescentQuartz, 1), ModItems.pureQuartz, "dustGlowstone"));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModItems.luminescentQuartz, 4), ModBlocks.blockLuminescentQuartz));
		//Luminescent Quartz Block
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.blockLuminescentQuartz, 1), "**", "**", '*', ModItems.luminescentQuartz));
		//Lens array
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModItems.lensArray, 2), "*&*", "@ $", "***", '*', ModItems.pureQuartz, '&', "gemEmerald", '@', "gemRuby", '$', "gemDiamond"));
		//Arcane Extractor
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.beamExtractor, 1), "***", "*# ", "***", '*', "obsidian", '#', ModItems.lensArray));
		//Quartz Stabilizer
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.quartzStabilizer, 1), " & ", "*&*", "***", '&', ModItems.luminescentQuartz, '*', ModItems.pureQuartz));
		//Crystalline Prism
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.crystallinePrism, 1), "*^*", "^&^", "*&*", '*', ModItems.pureQuartz, '^', ModItems.luminescentQuartz, '&', ModItems.lensArray));
		//Arcane Reflector
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.beamReflector, 1), "*^*", '*', "stone", '^', ModItems.pureQuartz));
		//Lens Frame
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.lensFrame, 1), "***", "*&*", "***", '*', "stone", '&', ModItems.pureQuartz));
		//Beam Redirector
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.beamRedirector, 1), "LRL", "***", "LRL", '*', ModItems.pureQuartz, 'L', ModItems.luminescentQuartz, 'R', "dustRedstone"));
		//Beam Siphon
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.beamSiphon, 1), "L L", "*A*", "L L", '*', ModItems.pureQuartz, 'L', ModItems.luminescentQuartz, 'A', ModItems.lensArray));
		//Beam Splitter
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.beamSplitter, 1), "LRL", "*A*", "LRL", '*', ModItems.pureQuartz, 'L', ModItems.luminescentQuartz, 'A', ModItems.lensArray, 'R', "dustRedstone"));
		//Color Chart
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.colorChart, 1), "RGB", "^^^", "___", '_', "slabWood", '^', "paper", 'R', "dyeRed", 'G', "dyeLime", 'B', "dyeBlue"));
		//Light Cluster
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.lightCluster, 8), ModItems.luminescentQuartz));
		//Crystalline Master Axis
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.crystalMasterAxis, 1), "*&*", "*#*", "***", '*', ModItems.pureQuartz, '#', ModBlocks.masterAxis, '&', ModItems.lensArray));
		//Ratiator
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.ratiator, 1), " * ", "*#*", "^^^", '*', ModItems.luminescentQuartz, '#', ModItems.pureQuartz, '^', "stone"));
		//Fat Feeder
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fatFeeder, 1), "***", "#A#", "***", '*', "ingotTin", '#', "netherrack", 'A', Items.GOLDEN_APPLE));
		//Detailed Crafter
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.detailedCrafter, 1), "*^*", "^&^", "*^*", '*', "ingotIron", '^', "ingotTin", '&', Blocks.CRAFTING_TABLE));
		//Basic Fluid Splitter
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.basicFluidSplitter, 1), "*^*", "&&&", "*^*", '*', "nuggetTin", '^', ModBlocks.fluidTube, '&', "ingotBronze"));
		//Redstone Fluid Splitter
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.fluidSplitter, 1), ModBlocks.basicFluidSplitter, "dustRedstone", "dustRedstone", "dustRedstone"));
		//Redstone Keyboard
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.redstoneKeyboard, 1), "&&&", "&*&", "&&&", '*', "ingotBronze", '&', "dustRedstone"));
		//Reagent Tank and Reaction Chamber emptying
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.reagentTankGlass, 1), new ItemStack(ModBlocks.reagentTankGlass, 1)));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.reactionChamberGlass, 1), new ItemStack(ModBlocks.reactionChamberGlass, 1)));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.reagentTankCrystal, 1), new ItemStack(ModBlocks.reagentTankCrystal, 1)));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.reactionChamberCrystal, 1), new ItemStack(ModBlocks.reactionChamberCrystal, 1)));
		//Wind Turbine
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.windTurbine, 1), "#*#", "*|*", "#*#", '|', "stickIron", '*', Blocks.WOOL, '#', "plankWood"));
		//Solar Heater
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.solarHeater, 1), "t t", "tct", "ttt", 't', "ingotTin", 'c', "ingotCopper"));
		//Heat Reservoir
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.heatReservoir, 1), "#*#", "***", "#*#", '#', "ingotCopper", '*', "dustSalt"));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.heatReservoir, 1), ModBlocks.heatReservoir));
		//Fertile Soil using Slag
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(EssentialsBlocks.fertileSoilWheat, 3), "#$#", "***", "^^^", '#', "itemSlag", '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', "cropWheat"));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(EssentialsBlocks.fertileSoilCarrot, 3), "#$#", "***", "^^^", '#', "itemSlag", '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', "cropCarrot"));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(EssentialsBlocks.fertileSoilPotato, 3), "#$#", "***", "^^^", '#', "itemSlag", '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', "cropPotato"));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(EssentialsBlocks.fertileSoilBeetroot, 3), "#$#", "***", "^^^", '#', "itemSlag", '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', Items.BEETROOT));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(EssentialsBlocks.fertileSoilNetherWart, 3), "#$#", "***", "^^^", '#', "itemSlag", '$', Items.FERMENTED_SPIDER_EYE, '^', Blocks.SOUL_SAND, '*', Items.NETHER_WART));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(EssentialsBlocks.fertileSoilOak, 3), "#$#", "***", "^^^", '#', "itemSlag", '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.OAK.getMetadata())));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(EssentialsBlocks.fertileSoilBirch, 3), "#$#", "***", "^^^", '#', "itemSlag", '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.BIRCH.getMetadata())));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(EssentialsBlocks.fertileSoilSpruce, 3), "#$#", "***", "^^^", '#', "itemSlag", '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.SPRUCE.getMetadata())));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(EssentialsBlocks.fertileSoilJungle, 3), "#$#", "***", "^^^", '#', "itemSlag", '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.JUNGLE.getMetadata())));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(EssentialsBlocks.fertileSoilDarkOak, 3), "#$#", "***", "^^^", '#', "itemSlag", '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.DARK_OAK.getMetadata())));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(EssentialsBlocks.fertileSoilAcacia, 3), "#$#", "***", "^^^", '#', "itemSlag", '$', Items.FERMENTED_SPIDER_EYE, '^', "dirt", '*', new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.ACACIA.getMetadata())));
		//Concrete Powder using slag
		for(EnumDyeColor color : EnumDyeColor.values()){
			toRegister.add(new ShapelessOreRecipe(null, new ItemStack(Blocks.CONCRETE_POWDER, 16, color.getMetadata()), "sand", "sand", "sand", "sand", "itemSlag", "itemSlag", "itemSlag", "itemSlag", "dye" + Character.toUpperCase(color.getName().charAt(0)) + color.getName().substring(1)));
		}
		//Stamp Mill
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.stampMill, 1), "|-|", "|I|", "|S|", '|', "plankWood", '-', "stickIron", 'I', "blockIron", 'S', "cobblestone"));
		//Ore Cleanser
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.oreCleanser, 1), "TWT", "T T", "TCT", 'T', "ingotTin", 'W', ModBlocks.waterCentrifuge, 'C', ModBlocks.fluidCoolingChamber));
		//Blast Furnace
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.blastFurnace, 1), "I|I", "B B", "BFB", '|', "stickIron", 'B', Blocks.BRICK_BLOCK, 'F', ModBlocks.fluidTube, 'I', "ingotIron"));
		//Stirling Engine
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.stirlingEngine, 1), "T|T", "C|C", "ICI", 'I', "ingotIron", 'C', "ingotCopper", '|', "stickIron", 'T', "ingotTin"));
		//Permeable Glass
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.permeableGlass, 4), " G ", "G*G", " G ", 'G', "blockGlass", '*', ModItems.pureQuartz));
		//Permeable Quartz
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.permeableQuartz, 4), " G ", "G*G", " G ", 'G', ModBlocks.blockPureQuartz, '*', "blockGlass"));
		//Redstone Transmitter
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.redstoneTransmitter, 1), "QRQ", "RTR", "QRQ", 'Q', ModItems.luminescentQuartz, 'R', "dustRedstone", 'T', Blocks.REDSTONE_TORCH));
		//Redstone Receiver
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.redstoneReceiver, 1), "QRQ", "RTR", "QRQ", 'Q', ModItems.luminescentQuartz, 'R', "dustRedstone", 'T', "blockRedstone"));
		//Dynamo
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.dynamo, 1), "-@-", "===", '@', "gearCopper", '-', "stickIron", '=', "ingotIron"));
		//Cavorite Block
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.cavorite, 1), "**", "**", '*', "gemCavorite"));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModItems.solidCavorite, 4), ModBlocks.cavorite));
		//Linking tool
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModItems.linkingTool, 1), EssentialsItems.wrench, "dustRedstone"));
		//Aqua Regia
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModItems.solidRegia, 4), ModItems.solidMuriatic, ModItems.solidMuriatic, ModItems.solidMuriatic, ModItems.solidFortis));
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

	private static void registerBoboItem(Item item, String configName, Predicate<ItemStack> ingr1, Predicate<ItemStack> ingr2, Predicate<ItemStack> ingr3){
		registerBoboItem(new ItemStack(item, 1), configName, ingr1, ingr2, ingr3);
	}

	@SuppressWarnings("unchecked")
	private static void registerBoboItem(ItemStack item, String configName, Predicate<ItemStack> ingr1, Predicate<ItemStack> ingr2, Predicate<ItemStack> ingr3){
		Property prop = ModConfig.config.get(ModConfig.CAT_BOBO, configName + " bobo-item recipe", true, "Default: true");
		ModConfig.boboItemProperties.add(prop);
		if(ModConfig.getConfigBool(prop, true)){
			EssentialsCrafting.brazierBoboRecipes.add(Pair.of(new Predicate[] {ingr1, ingr2, ingr3}, item));
		}
	}

	public static void initOreDict(){
		toRegisterOreDict.add(Pair.of(new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE), new String[] {"wool"}));
		toRegisterOreDict.add(Pair.of(Items.CHICKEN, new String[] {"meatRaw"}));
		toRegisterOreDict.add(Pair.of(Items.PORKCHOP, new String[] {"meatRaw"}));
		toRegisterOreDict.add(Pair.of(Items.BEEF, new String[] {"meatRaw"}));
		toRegisterOreDict.add(Pair.of(Items.RABBIT, new String[] {"meatRaw"}));
		toRegisterOreDict.add(Pair.of(Items.MUTTON, new String[] {"meatRaw"}));

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
	}
}

package com.Da_Technomancer.crossroads.items.crafting;

import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.alchemy.EnumReagents;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.CrossroadsConfig;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.fluids.BlockDistilledWater;
import com.Da_Technomancer.crossroads.fluids.BlockMoltenCopshowium;
import com.Da_Technomancer.crossroads.fluids.BlockSteam;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
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
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraftforge.common.ForgeConfigSpec;
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
		RecipeHolder.millRecipes.put(new ItemRecipePredicate(Items.BONE, 0), new ItemStack[] {new ItemStack(Items.DYE, 5, DyeColor.WHITE.getDyeDamage())});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("blockCoal"), new ItemStack[] {new ItemStack(Items.GUNPOWDER, 1)});
		RecipeHolder.millRecipes.put(new ItemRecipePredicate(Blocks.NETHER_WART_BLOCK, 0), new ItemStack[] {new ItemStack(Items.NETHER_WART, 9)});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("cropPotato"), new ItemStack[] {new ItemStack(CrossroadsItems.mashedPotato, 1)});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("gravel"), new ItemStack[] {new ItemStack(Items.FLINT)});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("blockRedstone"), new ItemStack[] {new ItemStack(Items.REDSTONE, 9)});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("cobblestone"), new ItemStack[] {new ItemStack(Blocks.SAND, 1)});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("stone"),  new ItemStack[] {new ItemStack(Blocks.GRAVEL, 1)});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("oreQuartz"),  new ItemStack[] {new ItemStack(Items.QUARTZ, 2)});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("oreDiamond"),  new ItemStack[] {new ItemStack(Items.DIAMOND, 2)});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("oreCoal"),  new ItemStack[] {new ItemStack(Items.COAL, 2)});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("oreRedstone"),  new ItemStack[] {new ItemStack(Items.REDSTONE, 10)});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("oreLapis"),  new ItemStack[] {new ItemStack(Items.DYE, 10, DyeColor.BLUE.getDyeDamage())});
		RecipeHolder.millRecipes.put(new OreDictCraftingStack("blockQuartz"),  new ItemStack[] {new ItemStack(Items.QUARTZ, 4)});

		RecipeHolder.dirtyWaterRecipes.add(Pair.of(5, new ItemStack(Items.GUNPOWDER)));
		RecipeHolder.dirtyWaterRecipes.add(Pair.of(5, new ItemStack(Items.DYE, 1, DyeColor.WHITE.getDyeDamage())));
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

		if(CrossroadsConfig.addBoboRecipes.getBoolean()){
			registerBoboItem(getFilledHopper(), "Vacuum Hopper", new ItemRecipePredicate(Blocks.HOPPER, 0), new OreDictCraftingStack("wool"), new ItemRecipePredicate(CrossroadsBlocks.fluidTube, 0));
			registerBoboItem(CrossroadsItems.magentaBread, "Magenta Bread", new ItemRecipePredicate(Items.BREAD, 0), new OreDictCraftingStack("dyeMagenta"), new OreDictCraftingStack("dustGlowstone"));
			registerBoboItem(CrossroadsItems.rainIdol, "Rain Idol", new OreDictCraftingStack("gemLapis"), new OreDictCraftingStack("cobblestone"), new OreDictCraftingStack("nuggetGold"));
			registerBoboItem(CrossroadsItems.squidHelmet, "Squid Helmet", new ItemRecipePredicate(Items.DYE, DyeColor.BLACK.getDyeDamage()), new ItemRecipePredicate(Items.FISH, 3), new OreDictCraftingStack("leather"));
			registerBoboItem(CrossroadsItems.pigZombieChestplate, "Zombie Pigman Chestplate", new ItemRecipePredicate(Items.BLAZE_POWDER, 0), new OreDictCraftingStack("leather"), new ItemRecipePredicate(Items.PORKCHOP, 0));
			registerBoboItem(CrossroadsItems.cowLeggings, "Cow Leggings", new ItemRecipePredicate(Items.MILK_BUCKET, 0), new OreDictCraftingStack("leather"), new ItemRecipePredicate(Items.BEEF, 0));
			registerBoboItem(CrossroadsItems.chickenBoots, "Chicken Boots", new OreDictCraftingStack("feather"), new OreDictCraftingStack("leather"), new ItemRecipePredicate(Blocks.WATERLILY, 0));
			registerBoboItem(CrossroadsItems.chaosRod, "Rod of Discord", new ItemRecipePredicate(Items.BLAZE_ROD, 0), new ItemRecipePredicate(Items.DRAGON_BREATH, 0), new ItemRecipePredicate(Items.GOLDEN_APPLE, -1));
			registerBoboItem(new ItemStack(CrossroadsBlocks.fluidVoid, 1), "Fluid Void", new ItemRecipePredicate(Blocks.SPONGE, 0), new ItemRecipePredicate(CrossroadsBlocks.fluidTube, 0), new ItemRecipePredicate(OreSetup.voidCrystal, 0));
			registerBoboItem(new ItemStack(CrossroadsBlocks.hamsterWheel, 1), "Hamster Wheel", new EdibleBlobRecipePredicate(4, 2), new ComponentCraftingStack("stick"), new OreDictCraftingStack("nuggetCopshowium"));
			registerBoboItem(CrossroadsItems.liechWrench, "Liechtensteinian Navy Wrench", (ItemStack s) -> EssentialsConfig.isWrench(s, false), new ItemRecipePredicate(CrossroadsItems.handCrank, 0), new ItemRecipePredicate(CrossroadsItems.staffTechnomancy, 0));
			registerBoboItem(new ItemStack(CrossroadsBlocks.maxwellDemon, 1), "Maxwell's Demon", new ItemRecipePredicate(Blocks.BEDROCK, 0), new EdibleBlobRecipePredicate(6, 4), new OreDictCraftingStack("ingotCopper"));
			registerBoboItem(new ItemStack(CrossroadsItems.nitroglycerin, 8), "Nitroglycerin", new OreDictCraftingStack("meatRaw"), new OreDictCraftingStack("gunpowder"), (Predicate<ItemStack>) (ItemStack stack) -> {
				if(stack.getItem() instanceof Phial){
					return CrossroadsItems.phialGlass.getReagants(stack).getQty(EnumReagents.NITRIC_ACID.id()) != 0;
				}
				return false;
			});
			registerBoboItem(new ItemStack(CrossroadsItems.poisonVodka, 1), "Poison Vodka", new ItemRecipePredicate(CrossroadsItems.solidVitriol, 0), new ItemRecipePredicate(Items.POISONOUS_POTATO, 0), (Predicate<ItemStack>) (ItemStack stack) -> stack.getItem() instanceof Phial || stack.getItem() == Items.GLASS_BOTTLE && stack.getCount() == 1);
			registerBoboItem(new ItemStack(CrossroadsItems.doublePoisonVodka, 1), "Double Poison Vodka", new ItemRecipePredicate(CrossroadsItems.solidVitriol, 0), new ItemRecipePredicate(Items.POISONOUS_POTATO, 0), new ItemRecipePredicate(CrossroadsItems.poisonVodka, 0));
		}



		RecipeHolder.beamExtractRecipes.put(Items.REDSTONE, new BeamUnit(18, 24, 0, 0));
		RecipeHolder.beamExtractRecipes.put(CrossroadsItems.dustSalt, new BeamUnit(0, 18, 24, 0));
		RecipeHolder.beamExtractRecipes.put(Items.COAL, new BeamUnit(24, 18, 0, 0));
		RecipeHolder.beamExtractRecipes.put(Items.GLOWSTONE_DUST, new BeamUnit(4, 4, 4, 0));
		RecipeHolder.beamExtractRecipes.put(CrossroadsItems.sulfur, new BeamUnit(32, 0, 0, 0));
		RecipeHolder.beamExtractRecipes.put(CrossroadsItems.solidQuicksilver, new BeamUnit(0, 32, 0, 0));
		RecipeHolder.beamExtractRecipes.put(CrossroadsItems.wasteSalt, new BeamUnit(0, 0, 32, 0));
		RecipeHolder.beamExtractRecipes.put(Items.ENDER_PEARL, new BeamUnit(32, 0, 32, 0));
		RecipeHolder.beamExtractRecipes.put(Items.DRAGON_BREATH, new BeamUnit(64, 0, 64, 0));
		RecipeHolder.beamExtractRecipes.put(Items.BLAZE_POWDER, new BeamUnit(32, 16, 0, 0));
		RecipeHolder.beamExtractRecipes.put(Items.GUNPOWDER, new BeamUnit(24, 0, 0, 0));
		RecipeHolder.beamExtractRecipes.put(Items.SLIME_BALL, new BeamUnit(0, 24, 0, 0));
		RecipeHolder.beamExtractRecipes.put(Items.SNOWBALL, new BeamUnit(0, 0, 24, 0));
		RecipeHolder.beamExtractRecipes.put(Items.SUGAR, new BeamUnit(8, 12, 0, 0));

		//Fusion beam
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.SNOW.getDefaultState(), false), new BeamTransmute(Blocks.ICE.getDefaultState(), 0));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.SAND.getDefaultState(), false), new BeamTransmute(CrossroadsBlocks.blockPureQuartz.getDefaultState(), 16));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.NETHERRACK.getDefaultState(), false), new BeamTransmute(Blocks.NETHER_BRICK.getDefaultState(), 8));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.COBBLESTONE.getDefaultState(), false), new BeamTransmute(Blocks.STONE.getDefaultState(), 0));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.DIRT.getDefaultState(), false), new BeamTransmute(Blocks.CLAY.getDefaultState(), 0));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.GRASS.getDefaultState(), true), new BeamTransmute(Blocks.CLAY.getDefaultState(), 0));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.STONE.getDefaultState().with(BlockStone.VARIANT, BlockStone.EnumType.STONE), false), new BeamTransmute(Blocks.STONEBRICK.getDefaultState(), 0));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.GRAVEL.getDefaultState(), false), new BeamTransmute(Blocks.PRISMARINE.getDefaultState(), 8));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.PRISMARINE.getDefaultState().with(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.ROUGH), false), new BeamTransmute(Blocks.PRISMARINE.getDefaultState().with(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.BRICKS), 16));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.PRISMARINE.getDefaultState().with(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.BRICKS), false), new BeamTransmute(Blocks.PRISMARINE.getDefaultState().with(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.DARK), 32));
		//Void fusion beam
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(Blocks.ICE.getDefaultState(), false), new BeamTransmute(Blocks.SNOW.getDefaultState(), 0));
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(CrossroadsBlocks.blockPureQuartz.getDefaultState(), false), new BeamTransmute(Blocks.SAND.getDefaultState(), 16));
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(Blocks.STONE.getDefaultState(), false), new BeamTransmute(Blocks.COBBLESTONE.getDefaultState(), 0));
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(Blocks.CLAY.getDefaultState(), false), new BeamTransmute(Blocks.DIRT.getDefaultState(), 0));
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(Blocks.STONEBRICK.getDefaultState(), false), new BeamTransmute(Blocks.STONE.getDefaultState(), 0));
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(Blocks.NETHER_BRICK.getDefaultState(), false), new BeamTransmute(Blocks.NETHERRACK.getDefaultState(), 8));
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(Blocks.PRISMARINE.getDefaultState(), false), new BeamTransmute(Blocks.GRAVEL.getDefaultState(), 8));
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(Blocks.PRISMARINE.getDefaultState().with(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.BRICKS), false), new BeamTransmute(Blocks.PRISMARINE.getDefaultState(), 16));
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(Blocks.PRISMARINE.getDefaultState().with(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.DARK), false), new BeamTransmute(Blocks.PRISMARINE.getDefaultState().with(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.BRICKS), 32));

		//Phial
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsItems.phialGlass, 1), "*", "*", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsItems.phialCrystal, 1), "*", "*", '*', "gemAlcCryst"));
		//Florence Flask
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsItems.florenceFlaskGlass, 1), " * ", "* *", "***", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsItems.florenceFlaskCrystal, 1), " * ", "* *", "***", '*', "gemAlcCryst"));
		//Shell
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsItems.shellGlass, 1), " * ", "* *", " * ", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsItems.shellCrystal, 1), " * ", "* *", " * ", '*', "gemAlcCryst"));
		//Alchemical Tube
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.alchemicalTubeGlass, 8), "***", "   ", "***", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.alchemicalTubeCrystal, 8), "***", "   ", "***", '*', "gemAlcCryst"));
		//Alembic
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.alembic, 1, 0), "** ", "***", "** ", '*', "ingotCopper"));
		//Chemical Vent
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.chemicalVent, 1, 0), "*#*", "###", "*#*", '*', "ingotTin", '#', Blocks.IRON_BARS));
		//Cooling Coil
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.coolingCoilGlass, 4), "* *", " * ", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.coolingCoilCrystal, 4), "* *", " * ", '*', "gemAlcCryst"));
		//Densus Plate
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.densusPlate, 6), "***", '*', "gemDensus"));
		RecipeHolder.alchemyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsItems.solidDensus, 1), CrossroadsBlocks.densusPlate, CrossroadsBlocks.densusPlate));
		//Anti-Densus plate
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.antiDensusPlate, 6), "***", '*', "gemAntiDensus"));
		RecipeHolder.alchemyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsItems.solidAntiDensus, 1), CrossroadsBlocks.antiDensusPlate, CrossroadsBlocks.antiDensusPlate));
		//Flow Limiter
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.flowLimiterGlass, 2), "*:*", '*', "blockGlass", ':', "ingotGold"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.flowLimiterCrystal, 2), "*:*", '*', "gemAlcCryst", ':', "ingotGold"));
		//Fluid Injector
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.fluidInjectorGlass, 1), "*|*", ": :", "*|*", '*', "ingotBronze", '|', CrossroadsBlocks.fluidTube, ':', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.fluidInjectorCrystal, 1), "*|*", ": :", "*|*", '*', "ingotBronze", '|', CrossroadsBlocks.fluidTube, ':', "gemAlcCryst"));
		//Glassware Holder
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.glasswareHolder, 1, 0), "^^^", "^ ^", '^', "nuggetIron"));
		//Heated Tube
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.heatedTubeGlass, 2), "*#*", '*', "blockGlass", '#', "ingotCopper"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.heatedTubeCrystal, 2), "*#*", '*', "gemAlcCryst", '#', "ingotCopper"));
		//Heat Limiter
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.heatLimiterBasic, 4, 0), "*&*", "*&*", "*#*", '*', "obsidian", '#', "dustRedstone", '&', "ingotCopper"));
		RecipeHolder.alchemyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsBlocks.heatLimiterRedstone, 1, 0), "dustRedstone", "dustRedstone", "dustRedstpme", CrossroadsBlocks.heatLimiterBasic));
		//Reaction Chamber
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.reactionChamberGlass, 1), "*^*", "^#^", "*^*", '*', "ingotBronze", '^', "blockGlass", '#', new ItemStack(CrossroadsBlocks.reagentTankGlass, 1)));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.reactionChamberCrystal, 1), "*^*", "^#^", "*^*", '*', "ingotBronze", '^', "gemAlcCryst", '#', new ItemStack(CrossroadsBlocks.reagentTankCrystal, 1)));
		//Reagent Pump
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.reagentPumpGlass, 1), "***", "*&*", "***", '&', "ingotBronze", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.reagentPumpCrystal, 1), "***", "*&*", "***", '&', "ingotBronze", '*', "gemAlcCryst"));
		//Reagent Tank
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.reagentTankGlass, 1), "*_*", "_ _", "*_*", '*', "ingotBronze", '_', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.reagentTankCrystal, 1), "*_*", "_ _", "*_*", '*', "ingotBronze", '_', "gemAlcCryst"));
		//Redstone Alchemical Tube
		RecipeHolder.alchemyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsBlocks.redsAlchemicalTubeGlass, 1), "dustRedstone", "dustRedstone", "dustRedstone", new ItemStack(CrossroadsBlocks.alchemicalTubeGlass, 1)));
		RecipeHolder.alchemyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsBlocks.redsAlchemicalTubeCrystal, 1), "dustRedstone", "dustRedstone", "dustRedstone", new ItemStack(CrossroadsBlocks.alchemicalTubeCrystal, 1)));
		//Enhanced Tesla Coil Tops
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.teslaCoilTopDistance, 1), "TTT", "TCT", "TTT", 'T', "ingotTin", 'C', CrossroadsBlocks.teslaCoilTopNormal));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.teslaCoilTopIntensity, 1), "TTT", "TCT", "TTT", 'T', "ingotGold", 'C', CrossroadsBlocks.teslaCoilTopNormal));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.teslaCoilTopAttack, 1), "TTT", "TCT", "TTT", 'T', "ingotCopper", 'C', CrossroadsBlocks.teslaCoilTopNormal));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.teslaCoilTopEfficiency, 1), "TTT", "TCT", "TTT", 'T', "ingotBronze", 'C', CrossroadsBlocks.teslaCoilTopNormal));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.teslaCoilTopDecorative, 1), "TTT", "TCT", "TTT", 'T', "blockGlass", 'C', CrossroadsBlocks.teslaCoilTopNormal));
		//Vanadium
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsItems.vanadiumOxide, 4), "***", "*B*", "***", '*', Items.COAL, 'B', "blockCoal"));
		//Charging Stand
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.chargingStand, 1), " * ", "| |", " ^ ", '*', "ingotIron", '|', "stickIron", '^', CrossroadsBlocks.glasswareHolder));
		//Atmos Charger
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.atmosCharger, 1), "| |", "| |", "*$*", '|', "stickIron", '*', "ingotIron", '$', CrossroadsItems.leydenJar));
		//Voltus Generator
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.voltusGenerator, 1), "*C*", "M$M", "*C*", 'M', "ingotCopper", 'C', CrossroadsItems.alchCrystal, '*', "ingotIron", '$', CrossroadsItems.leydenJar));
		//Detailed Crafting Table (Cheap Alchemy Recipe)
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.detailedCrafter, 1), "*&*", "&#&", "*&*", '*', "nuggetIron", '&', "nuggetTin", '#', Blocks.CRAFTING_TABLE));
		//Tesla Ray
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsItems.teslaRay, 1), "C C", "VII", "C C", 'C', "ingotCopshowium", 'I', "ingotIron", 'V', CrossroadsItems.leydenJar));
		//Damping Powder
		RecipeHolder.alchemyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsItems.dampingPowder, 4), CrossroadsItems.wasteSalt, CrossroadsItems.wasteSalt, "dustSalt", "dustRedstone"));
		//Reagent Filter
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.reagentFilterGlass, 1), "III", "|A|", "III", 'I', "ingotIron", '|', "blockGlass", 'A', CrossroadsItems.lensArray));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.reagentFilterCrystal, 1), "III", "|A|", "III", 'I', "ingotIron", '|', CrossroadsItems.alchCrystal, 'A', CrossroadsItems.lensArray));

		//Flying Machine
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsItems.flyingMachine, 1), "___", "@-@", "|+|", '_', "ingotBronze", '@', "gearCopshowium", '-', new ItemStack(CrossroadsBlocks.antiDensusPlate, 1), '+', new ItemStack(CrossroadsBlocks.densusPlate, 1), '|', "stickIron"));
		//Copshowium Creation Chamber
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.copshowiumCreationChamber, 1), "*^*", "^&^", "*^*", '*', CrossroadsItems.pureQuartz, '^', CrossroadsItems.luminescentQuartz, '&', CrossroadsBlocks.fluidCoolingChamber));
		//Gateway Frame
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.gatewayFrame, 3), "***", "^^^", "%^%", '*', Blocks.STONE, '^', "ingotCopshowium", '%', "obsidian"));
		//Beam Cage
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsItems.beamCage, 1), " L ", "*&*", " L ", '*', CrossroadsBlocks.quartzStabilizer, '&', "ingotCopshowium", 'L', CrossroadsItems.lensArray));
		//Cage Charger
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.cageCharger, 1), " B ", "QLQ", 'B', "ingotBronze", 'Q', CrossroadsItems.pureQuartz, 'L', CrossroadsItems.luminescentQuartz));
		//Beam Staff
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsItems.staffTechnomancy, 1), "*C*", " | ", " | ", '*', CrossroadsItems.lensArray, 'C', CrossroadsItems.beamCage, '|', "stickIron"));
		//Modular Goggles
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsItems.moduleGoggles, 1), "***", "^&^", '&', "ingotCopshowium", '*', "ingotBronze", '^', "blockGlass"));
		//Prototype Port
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.prototypePort, 1), "*&*", "& &", "*&*", '*', "ingotBronze", '&', "nuggetCopshowium"));
		//Prototyping Table
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.prototypingTable, 1), "*&*", "&%&", "*&*", '*', "ingotBronze", '&', "ingotCopshowium", '%', CrossroadsBlocks.detailedCrafter));
		//Redstone Axis
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.redstoneAxis, 1), "*^*", "^&^", "*^*", '*', "dustRedstone", '^', "nuggetBronze", '&', CrossroadsBlocks.masterAxis));
		//Math Axis
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.mathAxis, 1), "B|B", "GAG", "B|B", 'B', "nuggetBronze", '|', "stickIron", 'G', "gearCopshowium", 'A', CrossroadsBlocks.masterAxis));
		//Master Axis (Cheap Technomancy recipe)
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.masterAxis, 1), "***", "*#*", "*&*", '*', "nuggetIron", '#', "nuggetCopshowium", '&', "stickIron"));
		//Pistol
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsItems.pistol, 1), "CBB", "CA ", 'C', "ingotCopshowium", 'B', "ingotBronze", 'A', CrossroadsItems.lensArray));
		toRegister.add(new PrototypeItemSetRecipe(CrossroadsItems.pistol, "prot"));
		toRegister.add(new PrototypeItemClearRecipe(CrossroadsItems.pistol, "prot"));
		//Watch
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsItems.watch, 1), " * ", "*&*", " * ", '*', "ingotBronze", '&', "ingotCopshowium"));
		toRegister.add(new PrototypeItemSetRecipe(CrossroadsItems.watch, "prot"));
		toRegister.add(new PrototypeItemClearRecipe(CrossroadsItems.watch, "prot"));
		//Mechanical Arm
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.mechanicalArm, 1), " *|", " | ", "*I*", 'I', "blockIron", '|', "stickIron", '*', "gearCopshowium"));
		//Redstone Registry
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.redstoneRegistry, 1), "*&*", "&^&", "*&*", '*', "nuggetTin", '&', CrossroadsBlocks.redstoneKeyboard, '^', "ingotCopshowium"));
		//Detailed Crafting Table (Cheap Technomancy Recipe)
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.detailedCrafter, 1), "*&*", "&#&", "*&*", '*', "nuggetIron", '&', "nuggetTin", '#', Blocks.CRAFTING_TABLE));
		//Clockwork Stabilizer
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.clockworkStabilizer, 1), " # ", "#*#", " # ", '*', CrossroadsBlocks.quartzStabilizer, '#', "gearCopshowium"));
		//Beacon Harness
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.beaconHarness, 1), "^&^", "&*&", "^&^", '*', "ingotCopshowium", '&', CrossroadsItems.lensArray, '^', CrossroadsItems.luminescentQuartz));
		//Chrono Harness
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.chronoHarness, 1), "^&^", "&*&", "^&^", '*', "ingotCopshowium", '&', "ingotIron", '^', "blockRedstone"));
		//Flux node
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.fluxNode, 1), " | ", "|C|", "I|I", 'I', "ingotIron", '|', "stickIron", 'C', "ingotCopshowium"));
		//Temporal Accelerator
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.temporalAccelerator, 1), "CCC", "Q|Q", " | ", 'C', "ingotCopshowium", '|', "stickIron", 'Q', CrossroadsItems.luminescentQuartz));
		//Electric Flux Stabilizer
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.fluxStabilizerElectric, 1), "RCR", "CGC", "RCR", 'C', "ingotCopshowium", 'G', "blockGlass", 'R', "dustRedstone"));
		//Beam Flux Stabilizer
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.fluxStabilizerBeam, 1), "RCR", "CGC", "RCR", 'C', "ingotCopshowium", 'G', "blockGlass", 'R', CrossroadsItems.pureQuartz));
		//Electric Flux Crystal Stabilizer
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.fluxStabilizerCrystalElectric, 1), "RCR", "CGC", "RCR", 'C', "ingotCopshowium", 'G', CrossroadsItems.alchCrystal, 'R', "dustRedstone"));
		//Beam Flux Crystal Stabilizer
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.fluxStabilizerCrystalBeam, 1), "RCR", "CGC", "RCR", 'C', "ingotCopshowium", 'G', CrossroadsItems.alchCrystal, 'R', CrossroadsItems.pureQuartz));

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
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.fluidTube, 8), "###", "   ", "###", '#', "ingotBronze"));
		// Hand Crank
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsItems.handCrank, 1), " ?", "##", "$ ", '?', Blocks.LEVER, '#', "stickWood", '$', "cobblestone"));
		// Master Axis
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.masterAxis, 1), "###", "# #", "#$#", '#', "ingotIron", '$', "stickIron"));
		// Heating Crucible
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.heatingCrucible, 1), "# #", "#?#", "###", '#', Blocks.HARDENED_CLAY, '?', Items.CAULDRON));
		// Millstone
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.millstone, 1), "#$#", "#?#", "#$#", '#', "cobblestone", '?', "stickIron", '$', Blocks.PISTON));
		// Heat Cable
		for(HeatInsulators insul : HeatInsulators.values()){
			toRegister.add(new ShapedOreRecipe(null, new ItemStack(HeatCableFactory.HEAT_CABLES.get(insul), 4), "###", "???", "###", '#', insul.getItem(), '?', "ingotCopper"));
			toRegister.add(new ShapelessOreRecipe(null, new ItemStack(HeatCableFactory.REDSTONE_HEAT_CABLES.get(insul), 1), "dustRedstone", "dustRedstone", "dustRedstone", HeatCableFactory.HEAT_CABLES.get(insul)));
		}
		// Steam Boiler
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.steamBoiler, 1), "###", "# #", "&&&", '#', "ingotBronze", '&', "ingotCopper"));
		// Salt Block
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.blockSalt, 1), "##", "##", '#', "dustSalt"));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsItems.dustSalt, 4), "#", '#', "blockSalt"));
		// Rotary Pump
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.rotaryPump, 1), "#$#", "#$#", "&$&", '#', "ingotBronze", '&', "blockGlass", '$', "stickIron"));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsBlocks.rotaryPump, 1), CrossroadsBlocks.steamTurbine));
		// Steam Turbine
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsBlocks.steamTurbine, 1), CrossroadsBlocks.rotaryPump));
		// OmniMeter
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsItems.omnimeter, 1), " * ", "*#*", " * ", '*', "ingotBronze", '#', Items.COMPASS));
		// Fluid Tank (second recipe is for clearing contents)
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.fluidTank, 1), " $ ", "$#$", " $ ", '#', "ingotGold", '$', "ingotBronze"));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsBlocks.fluidTank, 1), CrossroadsBlocks.fluidTank));
		// Heat Exchanger
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.heatSink, 1), "###", "#$#", "###", '#', Blocks.IRON_BARS, '$', "ingotCopper"));
		// Firebox
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.firebox, 1), "#*#", "#@#", "###", '#', "cobblestone", '*', "ingotCopper", '@', Blocks.FURNACE));
		// Icebox
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.icebox, 1), "#*#", "#@#", "###", '#', "cobblestone", '*', "ingotCopper", '@', "gemLapis"));
		// Smelter
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.smelter, 1), "#*#", "# #", "###", '#', "ingotIron", '*', "ingotCopper"));
		// Salt Reactor
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.saltReactor, 1), "#$#", "$%$", "#@#", '#', "ingotIron", '$', CrossroadsBlocks.fluidTube, '%', "blockSalt", '@', "ingotCopper"));
		// Fluid Cooling Chamber
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.fluidCoolingChamber, 1), "###", "# #", "#%#", '#', "ingotIron", '%', "ingotCopper"));
		// Radiator
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.radiator, 1), "#$#", "#$#", "#$#", '#', CrossroadsBlocks.fluidTube, '$', "ingotIron"));
		// Rotary Drill
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.rotaryDrill, 2), " * ", "*#*", '*', "ingotIron", '#', "blockIron"));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.rotaryDrillGold, 1), " * ", "*#*", " * ", '*', "ingotGold", '#', CrossroadsBlocks.rotaryDrill));
		// Fat Collector
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.fatCollector, 1), "***", "# #", "*&*", '*', "ingotTin", '#', "netherrack", '&', "ingotCopper"));
		// Fat Congealer
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.fatCongealer, 1), "*^*", "# #", "* *", '*', "ingotTin", '#', "netherrack", '^', "stickIron"));
		//Redstone Fluid Tube
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsBlocks.redstoneFluidTube, 1), "dustRedstone", "dustRedstone", "dustRedstone", CrossroadsBlocks.fluidTube));
		//Water Centrifuge
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.waterCentrifuge, 1), "*&*", "^%^", "* *", '*', "ingotBronze", '&', "stickIron", '^', CrossroadsBlocks.fluidTube, '%', "ingotTin"));
		//Pure Quartz
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsItems.pureQuartz, 1), "dustSalt", "dustSalt", "gemQuartz"));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsItems.pureQuartz, 4), CrossroadsBlocks.blockPureQuartz));
		//Pure Quartz Block
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.blockPureQuartz, 1), "**", "**", '*', CrossroadsItems.pureQuartz));
		//Luminescent Quartz
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsItems.luminescentQuartz, 1), CrossroadsItems.pureQuartz, "dustGlowstone"));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsItems.luminescentQuartz, 4), CrossroadsBlocks.blockLuminescentQuartz));
		//Luminescent Quartz Block
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.blockLuminescentQuartz, 1), "**", "**", '*', CrossroadsItems.luminescentQuartz));
		//Lens array
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsItems.lensArray, 2), "*&*", "@ $", "***", '*', CrossroadsItems.pureQuartz, '&', "gemEmerald", '@', "gemRuby", '$', "gemDiamond"));
		//Arcane Extractor
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.beamExtractor, 1), "***", "*# ", "***", '*', "obsidian", '#', CrossroadsItems.lensArray));
		//Quartz Stabilizer
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.quartzStabilizer, 1), " & ", "*&*", "***", '&', CrossroadsItems.luminescentQuartz, '*', CrossroadsItems.pureQuartz));
		//Crystalline Prism
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.crystallinePrism, 1), "*^*", "^&^", "*&*", '*', CrossroadsItems.pureQuartz, '^', CrossroadsItems.luminescentQuartz, '&', CrossroadsItems.lensArray));
		//Arcane Reflector
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.beamReflector, 1), "*^*", '*', "stone", '^', CrossroadsItems.pureQuartz));
		//Lens Frame
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.lensFrame, 1), "***", "*&*", "***", '*', "stone", '&', CrossroadsItems.pureQuartz));
		//Beam Redirector
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.beamRedirector, 1), "LRL", "***", "LRL", '*', CrossroadsItems.pureQuartz, 'L', CrossroadsItems.luminescentQuartz, 'R', "dustRedstone"));
		//Beam Siphon
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.beamSiphon, 1), "L L", "*A*", "L L", '*', CrossroadsItems.pureQuartz, 'L', CrossroadsItems.luminescentQuartz, 'A', CrossroadsItems.lensArray));
		//Beam Splitter
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.beamSplitter, 1), "LRL", "*A*", "LRL", '*', CrossroadsItems.pureQuartz, 'L', CrossroadsItems.luminescentQuartz, 'A', CrossroadsItems.lensArray, 'R', "dustRedstone"));
		//Color Chart
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.colorChart, 1), "RGB", "^^^", "___", '_', "slabWood", '^', "paper", 'R', "dyeRed", 'G', "dyeLime", 'B', "dyeBlue"));
		//Light Cluster
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsBlocks.lightCluster, 8), CrossroadsItems.luminescentQuartz));
		//Crystalline Master Axis
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.crystalMasterAxis, 1), "*&*", "*#*", "***", '*', CrossroadsItems.pureQuartz, '#', CrossroadsBlocks.masterAxis, '&', CrossroadsItems.lensArray));
		//Ratiator
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.ratiator, 1), " * ", "*#*", "^^^", '*', CrossroadsItems.luminescentQuartz, '#', CrossroadsItems.pureQuartz, '^', "stone"));
		//Fat Feeder
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.fatFeeder, 1), "***", "#A#", "***", '*', "ingotTin", '#', "netherrack", 'A', Items.GOLDEN_APPLE));
		//Detailed Crafter
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.detailedCrafter, 1), "*^*", "^&^", "*^*", '*', "ingotIron", '^', "ingotTin", '&', Blocks.CRAFTING_TABLE));
		//Basic Fluid Splitter
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.basicFluidSplitter, 1), "*^*", "&&&", "*^*", '*', "nuggetTin", '^', CrossroadsBlocks.fluidTube, '&', "ingotBronze"));
		//Redstone Fluid Splitter
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsBlocks.fluidSplitter, 1), CrossroadsBlocks.basicFluidSplitter, "dustRedstone", "dustRedstone", "dustRedstone"));
		//Redstone Keyboard
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.redstoneKeyboard, 1), "&&&", "&*&", "&&&", '*', "ingotBronze", '&', "dustRedstone"));
		//Reagent Tank and Reaction Chamber emptying
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsBlocks.reagentTankGlass, 1), new ItemStack(CrossroadsBlocks.reagentTankGlass, 1)));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsBlocks.reactionChamberGlass, 1), new ItemStack(CrossroadsBlocks.reactionChamberGlass, 1)));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsBlocks.reagentTankCrystal, 1), new ItemStack(CrossroadsBlocks.reagentTankCrystal, 1)));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsBlocks.reactionChamberCrystal, 1), new ItemStack(CrossroadsBlocks.reactionChamberCrystal, 1)));
		//Wind Turbine
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.windTurbine, 1), "#*#", "*|*", "#*#", '|', "stickIron", '*', Blocks.WOOL, '#', "plankWood"));
		//Solar Heater
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.solarHeater, 1), "t t", "tct", "ttt", 't', "ingotTin", 'c', "ingotCopper"));
		//Heat Reservoir
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.heatReservoir, 1), "#*#", "***", "#*#", '#', "ingotCopper", '*', "dustSalt"));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsBlocks.heatReservoir, 1), CrossroadsBlocks.heatReservoir));
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
		for(DyeColor color : DyeColor.values()){
			toRegister.add(new ShapelessOreRecipe(null, new ItemStack(Blocks.CONCRETE_POWDER, 16, color.getMetadata()), "sand", "sand", "sand", "sand", "itemSlag", "itemSlag", "itemSlag", "itemSlag", "dye" + Character.toUpperCase(color.getName().charAt(0)) + color.getName().substring(1)));
		}
		//Stamp Mill
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.stampMill, 1), "|-|", "|I|", "|S|", '|', "plankWood", '-', "stickIron", 'I', "blockIron", 'S', "cobblestone"));
		//Ore Cleanser
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.oreCleanser, 1), "TWT", "T T", "TCT", 'T', "ingotTin", 'W', CrossroadsBlocks.waterCentrifuge, 'C', CrossroadsBlocks.fluidCoolingChamber));
		//Blast Furnace
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.blastFurnace, 1), "I|I", "B B", "BFB", '|', "stickIron", 'B', Blocks.BRICK_BLOCK, 'F', CrossroadsBlocks.fluidTube, 'I', "ingotIron"));
		//Stirling Engine
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.stirlingEngine, 1), "T|T", "C|C", "ICI", 'I', "ingotIron", 'C', "ingotCopper", '|', "stickIron", 'T', "ingotTin"));
		//Permeable Glass
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.permeableGlass, 4), " G ", "G*G", " G ", 'G', "blockGlass", '*', CrossroadsItems.pureQuartz));
		//Permeable Quartz
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.permeableQuartz, 4), " G ", "G*G", " G ", 'G', CrossroadsBlocks.blockPureQuartz, '*', "blockGlass"));
		//Redstone Transmitter
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.redstoneTransmitter, 1), "QRQ", "RTR", "QRQ", 'Q', CrossroadsItems.luminescentQuartz, 'R', "dustRedstone", 'T', Blocks.REDSTONE_TORCH));
		//Redstone Receiver
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.redstoneReceiver, 1), "QRQ", "RTR", "QRQ", 'Q', CrossroadsItems.luminescentQuartz, 'R', "dustRedstone", 'T', "blockRedstone"));
		//Dynamo
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.dynamo, 1), "-@-", "===", '@', "gearCopper", '-', "stickIron", '=', "ingotIron"));
		//Cavorite Block
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.cavorite, 2), "**", "**", '*', "gemCavorite"));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsItems.solidCavorite, 2), CrossroadsBlocks.cavorite));
		//Linking tool
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsItems.linkingTool, 1), EssentialsItems.wrench, "dustRedstone"));
		//Aqua Regia
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsItems.solidRegia, 4), CrossroadsItems.solidMuriatic, CrossroadsItems.solidMuriatic, CrossroadsItems.solidMuriatic, CrossroadsItems.solidFortis));
		//Leyden Jar
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsItems.leydenJar, 1), " | ", "*r*", "***", '|', "stickIron", 'r', "dustRedstone", '*', "nuggetIron"));
		//Tesla Coil
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.teslaCoil, 2), "|||", "^*^", "|||", '*', "ingotCopper", '|', "ingotIron", '^', "dustRedstone"));
		//Tesla Coil Tops
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.teslaCoilTopNormal, 2), "III", " C ", "RCR", 'C', "ingotCopper", 'I', "ingotIron", 'R', "dustRedstone"));

	}

	private static ItemStack getFilledHopper(){
		ItemStack stack = new ItemStack(Blocks.HOPPER);

		CompoundNBT nbt = new CompoundNBT();
		ListNBT nbttag = new ListNBT();
		CompoundNBT nbttagcompound = new CompoundNBT();
		nbttagcompound.setByte("Slot", (byte) 0);
		new ItemStack(CrossroadsItems.vacuum).writeToNBT(nbttagcompound);
		nbttag.appendTag(nbttagcompound);
		nbt.setTag("Items", nbttag);
		stack.setTagInfo("BlockEntityTag", nbt);

		CompoundNBT nbttagcompound1 = new CompoundNBT();
		ListNBT nbttaglist = new ListNBT();
		nbttaglist.appendTag(new StringNBT("(+NBT)"));
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
		Property prop = CrossroadsConfig.config.get(CrossroadsConfig.CAT_BOBO, configName + " bobo-item recipe", true, "Default: true");
		CrossroadsConfig.boboItemProperties.add(prop);
		if(((ForgeConfigSpec.BooleanValue) prop).get()){
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
				throw Crossroads.logger.throwing(new ClassCastException("INVALID object in toRegisterOreDict: " + left + "; Must be Block, Item, or ItemStack."));
			}

		}
		toRegisterOreDict.clear();
	}
}

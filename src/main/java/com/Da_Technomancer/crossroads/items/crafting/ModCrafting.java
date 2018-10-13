package com.Da_Technomancer.crossroads.items.crafting;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.API.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.rotary.GearTypes;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.fluids.*;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.items.itemSets.HeatCableFactory;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.items.crafting.EssentialsCrafting;
import net.minecraft.block.Block;
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
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.function.Predicate;

public final class ModCrafting{

	public static final ArrayList<IRecipe> toRegister = new ArrayList<IRecipe>();
	/**
	 * The Object should either be a Block, Item, or ItemStack. The String[] contains keys to register it under. 
	 */
	public static final ArrayList<Pair<Object, String[]>> toRegisterOreDict = new ArrayList<Pair<Object, String[]>>();

	@SuppressWarnings("unchecked")
	public static void init(){

		RecipeHolder.grindRecipes.put(new OreDictCraftingStack("oreCopper"), new ItemStack[] {MiscOp.getOredictStack("dustCopper", 2), new ItemStack(Blocks.SAND)});
		RecipeHolder.grindRecipes.put(new ItemRecipePredicate(Items.WHEAT, 0), new ItemStack[] {new ItemStack(Items.WHEAT_SEEDS, 3)});
		RecipeHolder.grindRecipes.put(new ItemRecipePredicate(Blocks.PUMPKIN, 0), new ItemStack[] {new ItemStack(Items.PUMPKIN_SEEDS, 8)});
		RecipeHolder.grindRecipes.put(new ItemRecipePredicate(Items.MELON, 0), new ItemStack[] {new ItemStack(Items.MELON_SEEDS, 3)});
		RecipeHolder.grindRecipes.put(new ItemRecipePredicate(Items.BONE, 0), new ItemStack[] {new ItemStack(Items.DYE, 5, EnumDyeColor.WHITE.getDyeDamage())});
		RecipeHolder.grindRecipes.put(new OreDictCraftingStack("blockCoal"), new ItemStack[] {new ItemStack(Items.GUNPOWDER, 1)});
		RecipeHolder.grindRecipes.put(new ItemRecipePredicate(Blocks.NETHER_WART_BLOCK, 0), new ItemStack[] {new ItemStack(Items.NETHER_WART, 9)});
		RecipeHolder.grindRecipes.put(new OreDictCraftingStack("cropPotato"), new ItemStack[] {new ItemStack(ModItems.mashedPotato, 1)});
		RecipeHolder.grindRecipes.put(new OreDictCraftingStack("gravel"), new ItemStack[] {new ItemStack(Items.FLINT)});
		RecipeHolder.grindRecipes.put(new OreDictCraftingStack("blockRedstone"), new ItemStack[] {new ItemStack(Items.REDSTONE, 9)});
		RecipeHolder.grindRecipes.put(new OreDictCraftingStack("cobblestone"), new ItemStack[] {new ItemStack(Blocks.SAND, 1)});
		RecipeHolder.grindRecipes.put(new OreDictCraftingStack("ingotCopper"), new ItemStack[] {MiscOp.getOredictStack("dustCopper", 1)});
		RecipeHolder.grindRecipes.put(new OreDictCraftingStack("stone"),  new ItemStack[] {new ItemStack(Blocks.GRAVEL, 1)});

		RecipeHolder.heatingCrucibleRecipes.add(Triple.of(new OreDictCraftingStack("ingotIron"), new FluidStack(BlockMoltenIron.getMoltenIron(), 144), "minecraft:blocks/iron_block"));
		RecipeHolder.heatingCrucibleRecipes.add(Triple.of(new OreDictCraftingStack("ingotGold"), new FluidStack(BlockMoltenGold.getMoltenGold(), 144), "minecraft:blocks/gold_block"));
		RecipeHolder.heatingCrucibleRecipes.add(Triple.of(new OreDictCraftingStack("ingotTin"), new FluidStack(BlockMoltenTin.getMoltenTin(), 144), Main.MODID + ":blocks/block_tin"));
		RecipeHolder.heatingCrucibleRecipes.add(Triple.of(new OreDictCraftingStack("ingotCopper"), new FluidStack(BlockMoltenCopper.getMoltenCopper(), 144), Main.MODID + ":blocks/block_copper"));
		RecipeHolder.heatingCrucibleRecipes.add(Triple.of(new OreDictCraftingStack("dustCopper"), new FluidStack(BlockMoltenCopper.getMoltenCopper(), 144), Main.MODID + ":blocks/ore_native_copper"));
		RecipeHolder.heatingCrucibleRecipes.add(Triple.of(new OreDictCraftingStack("oreCopper"), new FluidStack(BlockMoltenCopper.getMoltenCopper(), 144), Main.MODID + ":blocks/ore_copper"));
		RecipeHolder.heatingCrucibleRecipes.add(Triple.of(new OreDictCraftingStack("cobblestone"), new FluidStack(FluidRegistry.LAVA, 200), "minecraft:blocks/cobblestone"));

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

		if(ModConfig.addBoboRecipes.getBoolean()){
			EssentialsCrafting.brazierBoboRecipes.add(Pair.of(new Predicate[] {new ItemRecipePredicate(Blocks.HOPPER, 0), new OreDictCraftingStack("wool"), new ItemRecipePredicate(ModBlocks.fluidTube, 0)}, getFilledHopper()));
			EssentialsCrafting.brazierBoboRecipes.add(Pair.of(new Predicate[] {new ItemRecipePredicate(Items.BREAD, 0), new OreDictCraftingStack("dyeMagenta"), new OreDictCraftingStack("dustGlowstone")}, new ItemStack(ModItems.magentaBread)));
			if(ModConfig.weatherControl.getBoolean()){
				EssentialsCrafting.brazierBoboRecipes.add(Pair.of(new Predicate[] {new OreDictCraftingStack("gemLapis"), new OreDictCraftingStack("cobblestone"), new OreDictCraftingStack("nuggetGold")}, new ItemStack(ModItems.rainIdol, 1)));
			}
			EssentialsCrafting.brazierBoboRecipes.add(Pair.of(new Predicate[] {new OreDictCraftingStack("feather"), new OreDictCraftingStack("leather"), new ItemRecipePredicate(Blocks.WATERLILY, 0)}, new ItemStack(ModItems.chickenBoots, 1)));
			EssentialsCrafting.brazierBoboRecipes.add(Pair.of(new Predicate[] {new ItemRecipePredicate(Items.DYE, EnumDyeColor.BLACK.getDyeDamage()), new ItemRecipePredicate(Items.FISH, 3), new OreDictCraftingStack("leather")}, new ItemStack(ModItems.squidHelmet, 1)));
			EssentialsCrafting.brazierBoboRecipes.add(Pair.of(new Predicate[] {new ItemRecipePredicate(Items.BLAZE_POWDER, 0), new OreDictCraftingStack("leather"), new ItemRecipePredicate(Items.PORKCHOP, 0)}, new ItemStack(ModItems.pigZombieChestplate, 1)));
			EssentialsCrafting.brazierBoboRecipes.add(Pair.of(new Predicate[] {new ItemRecipePredicate(Items.MILK_BUCKET, 0), new OreDictCraftingStack("leather"), new ItemRecipePredicate(Items.BEEF, 0)}, new ItemStack(ModItems.cowLeggings, 1)));
			EssentialsCrafting.brazierBoboRecipes.add(Pair.of(new Predicate[] {new ItemRecipePredicate(Items.BLAZE_ROD, 0), new ItemRecipePredicate(Items.DRAGON_BREATH, 0), new ItemRecipePredicate(Items.GOLDEN_APPLE, -1)}, new ItemStack(ModItems.chaosRod, 1)));
			EssentialsCrafting.brazierBoboRecipes.add(Pair.of(new Predicate[] {new ItemRecipePredicate(Blocks.SPONGE, 0), new ItemRecipePredicate(ModBlocks.fluidTube, 0), new ItemRecipePredicate(ModItems.voidCrystal, 0)}, new ItemStack(ModBlocks.fluidVoid, 1)));
			EssentialsCrafting.brazierBoboRecipes.add(Pair.of(new Predicate[] {new EdibleBlobRecipePredicate(4, 2), new OreDictCraftingStack("stickIron"), new OreDictCraftingStack("nuggetCopshowium")}, new ItemStack(ModBlocks.hamsterWheel, 1)));
			EssentialsCrafting.brazierBoboRecipes.add(Pair.of(new Predicate[] {(Object s) -> EssentialsConfig.isWrench((ItemStack) s, false), new ItemRecipePredicate(ModItems.handCrank, 0), new ItemRecipePredicate(ModItems.staffTechnomancy, 0)}, new ItemStack(ModItems.liechWrench, 1)));
			EssentialsCrafting.brazierBoboRecipes.add(Pair.of(new Predicate[] {new ItemRecipePredicate(Blocks.BEDROCK, 0), new EdibleBlobRecipePredicate(6, 4), new OreDictCraftingStack("ingotCopper")}, new ItemStack(ModBlocks.maxwellDemon, 1)));
			EssentialsCrafting.brazierBoboRecipes.add(Pair.of(new Predicate[] {new OreDictCraftingStack("meatRaw"), new OreDictCraftingStack("gunpowder"), (Predicate<ItemStack>) (ItemStack stack) -> {
				if(stack.getItem() == ModItems.phial){
					ReagentStack reag = ModItems.phial.getReagants(stack).getLeft()[5];
					return reag != null && reag.getAmount() >= 5;
				}
				return false;
			}}, new ItemStack(ModItems.nitroglycerin, 8)));
		}

		RecipeHolder.magExtractRecipes.put(Items.REDSTONE, new MagicUnit(24, 36, 0, 0));
		RecipeHolder.magExtractRecipes.put(ModItems.dustSalt, new MagicUnit(0, 24, 36, 0));
		RecipeHolder.magExtractRecipes.put(Items.COAL, new MagicUnit(36, 24, 0, 0));
		RecipeHolder.magExtractRecipes.put(Items.GLOWSTONE_DUST, new MagicUnit(1, 1, 1, 0));
		RecipeHolder.magExtractRecipes.put(ModItems.sulfur, new MagicUnit(60, 0, 0, 0));
		RecipeHolder.magExtractRecipes.put(ModItems.solidQuicksilver, new MagicUnit(0, 60, 0, 0));
		RecipeHolder.magExtractRecipes.put(ModItems.wasteSalt, new MagicUnit(0, 0, 60, 0));

		//Fusion beam
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.SNOW.getDefaultState(), false), new BeamTransmute(Blocks.ICE.getDefaultState(), 1));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.SAND.getDefaultState(), false), new BeamTransmute(ModBlocks.blockPureQuartz.getDefaultState(), 16));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.NETHERRACK.getDefaultState(), false), new BeamTransmute(Blocks.NETHER_BRICK.getDefaultState(), 8));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.GRAVEL.getDefaultState(), false), new BeamTransmute(Blocks.PRISMARINE.getDefaultState(), 8));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.COBBLESTONE.getDefaultState(), false), new BeamTransmute(Blocks.STONE.getDefaultState(), 1));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.STONE), false), new BeamTransmute(Blocks.STONEBRICK.getDefaultState(), 1));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.ROUGH), false), new BeamTransmute(Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.BRICKS), 12));
		RecipeHolder.fusionBeamRecipes.put(new BlockRecipePredicate(Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.BRICKS), false), new BeamTransmute(Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.DARK), 16));
		//Void fusion beam
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(Blocks.ICE.getDefaultState(), false), new BeamTransmute(Blocks.SNOW.getDefaultState(), 0));
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(ModBlocks.blockPureQuartz.getDefaultState(), false), new BeamTransmute(Blocks.SAND.getDefaultState(), 16));
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(Blocks.STONE.getDefaultState(), false), new BeamTransmute(Blocks.COBBLESTONE.getDefaultState(), 1));
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(Blocks.STONEBRICK.getDefaultState(), false), new BeamTransmute(Blocks.STONE.getDefaultState(), 1));
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(Blocks.NETHER_BRICK.getDefaultState(), false), new BeamTransmute(Blocks.NETHERRACK.getDefaultState(), 8));
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(Blocks.PRISMARINE.getDefaultState(), false), new BeamTransmute(Blocks.GRAVEL.getDefaultState(), 8));
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.BRICKS), false), new BeamTransmute(Blocks.PRISMARINE.getDefaultState(), 12));
		RecipeHolder.vFusionBeamRecipes.put(new BlockRecipePredicate(Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.DARK), false), new BeamTransmute(Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.BRICKS), 16));

//		//Custom tool
//		//(sword)
//		RecipeHolder.alchemyRecipes.add(new CustomToolRecipe("sword", new byte[][] {{1, 0, 0}, {1, 0, 0}, {2, 0, 0}}));
//		RecipeHolder.alchemyRecipes.add(new CustomToolRecipe("sword", new byte[][] {{0, 1, 0}, {0, 1, 0}, {0, 2, 0}}));
//		RecipeHolder.alchemyRecipes.add(new CustomToolRecipe("sword", new byte[][] {{0, 0, 1}, {0, 0, 1}, {0, 0, 2}}));
//		//(pickaxe)
//		RecipeHolder.alchemyRecipes.add(new CustomToolRecipe("pickaxe", new byte[][] {{1, 1, 1}, {0, 2, 0}, {0, 2, 0}}));
//		//(shovel)
//		RecipeHolder.alchemyRecipes.add(new CustomToolRecipe("shovel", new byte[][] {{1, 0, 0}, {2, 0, 0}, {2, 0, 0}}));
//		RecipeHolder.alchemyRecipes.add(new CustomToolRecipe("shovel", new byte[][] {{0, 1, 0}, {0, 2, 0}, {0, 2, 0}}));
//		RecipeHolder.alchemyRecipes.add(new CustomToolRecipe("shovel", new byte[][] {{0, 0, 1}, {0, 0, 2}, {0, 0, 2}}));
//		//(axe)
//		RecipeHolder.alchemyRecipes.add(new CustomToolRecipe("axe", new byte[][] {{1, 1, 0}, {1, 2, 0}, {0, 2, 0}}));
//		RecipeHolder.alchemyRecipes.add(new CustomToolRecipe("axe", new byte[][] {{0, 1, 1}, {0, 2, 1}, {0, 2, 0}}));
//		RecipeHolder.alchemyRecipes.add(new CustomToolRecipe("axe", new byte[][] {{1, 1, 0}, {2, 1, 0}, {2, 0, 0}}));
//		RecipeHolder.alchemyRecipes.add(new CustomToolRecipe("axe", new byte[][] {{0, 1, 1}, {0, 1, 2}, {0, 0, 2}}));
		//Phial
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.phial, 2, 0), "*", "*", "*", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.phial, 2, 1), "*", "*", "*", '*', "gemAlcCryst"));
		//Florence Flask
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.florenceFlask, 1, 0), " * ", "* *", "***", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.florenceFlask, 1, 1), " * ", "* *", "***", '*', "gemAlcCryst"));
		//Shell
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.shell, 1, 0), " * ", "* *", " * ", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.shell, 1, 1), " * ", "* *", " * ", '*', "gemAlcCryst"));
		//Leyden Jar
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.leydenJar, 1), " | ", "*r*", "***", '|', "stickIron", 'r', "dustRedstone", '*', "nuggetIron"));
		//Alchemical Tube
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.alchemicalTube, 8, 0), "***", "   ", "***", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.alchemicalTube, 8, 1), "***", "   ", "***", '*', "gemAlcCryst"));
		//Alchemy Chart
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.alchemyChart, 1, 0), "***", "*#*", "***", '*', ModItems.wasteSalt, '#', ModBlocks.colorChart));
		//Alembic
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.alembic, 1, 0), "** ", "***", "** ", '*', "ingotCopper"));
		//Chemical Vent
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.chemicalVent, 1, 0), "*#*", "###", "*#*", '*', "ingotTin", '#', Blocks.IRON_BARS));
		//Cooling Coil
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.coolingCoil, 4, 0), "* *", " * ", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.coolingCoil, 4, 1), "* *", " * ", '*', "gemAlcCryst"));
		//Densus Plate
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.densusPlate, 6, 0), "***", '*', "gemDensus"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.densusPlate, 6, 1), "***", '*', "gemAntiDensus"));
		//Dynamo
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.dynamo, 1, 0), "-@-", "===", '@', "gearCopper", '-', "stickIron", '=', "ingotIron"));
		//Flow Limiter
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.flowLimiter, 2, 0), "*:*", '*', "blockGlass", ':', "ingotGold"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.flowLimiter, 2, 1), "*:*", '*', "gemAlcCryst", ':', "ingotGold"));
		//Fluid Injector
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fluidInjector, 1, 0), "*|*", ": :", "*|*", '*', "ingotBronze", '|', ModBlocks.fluidTube, ':', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.fluidInjector, 1, 1), "*|*", ": :", "*|*", '*', "ingotBronze", '|', ModBlocks.fluidTube, ':', "gemAlcCryst"));
		//Glassware Holder
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.glasswareHolder, 1, 0), "^^^", "^ ^", '^', "nuggetIron"));
		//Heated Tube
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.heatedTube, 2, 0), "*#*", '*', "blockGlass", '#', "ingotCopper"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.heatedTube, 2, 1), "*#*", '*', "gemAlcCryst", '#', "ingotCopper"));
		//Heat Limiter
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.heatLimiter, 4, 0), "*&*", "*&*", "*#*", '*', "obsidian", '#', "dustRedstone", '&', "ingotCopper"));
		//Reaction Chamber
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.reactionChamber, 1, 0), "*^*", "^#^", "*^*", '*', "ingotBronze", '^', "blockGlass", '#', new ItemStack(ModBlocks.reagentTank, 1, 0)));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.reactionChamber, 1, 1), "*^*", "^#^", "*^*", '*', "ingotBronze", '^', "gemAlcCryst", '#', new ItemStack(ModBlocks.reagentTank, 1, 1)));
		//Reagent Pump
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.reagentPump, 1, 0), "***", "*&*", "***", '&', "ingotBronze", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.reagentPump, 1, 1), "***", "*&*", "***", '&', "ingotBronze", '*', "gemAlcCryst"));
		//Reagent Tank
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.reagentTank, 1, 0), "*_*", "_ _", "*_*", '*', "ingotBronze", '_', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.reagentTank, 1, 1), "*_*", "_ _", "*_*", '*', "ingotBronze", '_', "gemAlcCryst"));
		//Redstone Alchemical Tube
		RecipeHolder.alchemyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.redsAlchemicalTube, 1, 0), "dustRedstone", "dustRedstone", "dustRedstone", new ItemStack(ModBlocks.alchemicalTube, 1, 0)));
		RecipeHolder.alchemyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.redsAlchemicalTube, 1, 1), "dustRedstone", "dustRedstone", "dustRedstone", new ItemStack(ModBlocks.alchemicalTube, 1, 1)));
		//Tesla Coil
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.teslaCoil, 2), "***", " | ", "|^|", '*', "ingotCopper", '|', "ingotIron", '^', "dustRedstone"));
		//Vanadium
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.vanadium, 4), "***", "*B*", "***", '*', Items.COAL, 'B', "blockCoal"));
		//Charging Stand
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.chargingStand, 1), " * ", "| |", " ^ ", '*', "ingotIron", '|', "stickIron", '^', ModBlocks.glasswareHolder));
		//Atmos Charger
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.atmosCharger, 1), "| |", "| |", "*$*", '|', "stickIron", '*', "ingotIron", '$', ModItems.leydenJar));
		//Detailed Crafting Table (Cheap Alchemy Recipe)
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.detailedCrafter, 1), "*&*", "&#&", "*&*", '*', "nuggetIron", '&', "nuggetTin", '#', Blocks.CRAFTING_TABLE));
		//Tesla Ray
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.teslaRay, 1), "C C", "VII", "C C", 'C', "ingotCopshowium", 'I', "ingotIron", 'V', ModItems.leydenJar));


		//Flying Machine
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.flyingMachine, 1), "___", "@-@", "|+|", '_', "ingotBronze", '@', "gearCopshowium", '-', new ItemStack(ModBlocks.densusPlate, 1, 1), '+', new ItemStack(ModBlocks.densusPlate, 1, 0), '|', "stickIron"));
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
		//Copshowium Axle
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModItems.axleCopshowium, 2), "*", "|", "*", '*', "ingotCopshowium", '|', "stickWood"));
		//Clockwork Stabilizer
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.clockworkStabilizer, 1), " # ", "#*#", " # ", '*', ModBlocks.largeQuartzStabilizer, '#', "gearCopshowium"));

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
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModItems.axleIron, 2), "#", "?", "#", '#', "ingotIron", '?', "stickWood"));
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
		//Small Gear
		//Toggle Gear
		//Large Gear
		for(GearTypes type : GearTypes.values()){
			int index = type.ordinal();
			toRegister.add(new ShapedOreRecipe(null, new ItemStack(GearFactory.BASIC_GEARS[index], 9), " ? ", "?#?", " ? ", '#', "block" + type.toString(), '?', "ingot" + type.toString()));
			toRegister.add(new ShapedOreRecipe(null, new ItemStack(GearFactory.BASIC_GEARS[index], 1), " ? ", "?#?", " ? ", '#', "ingot" + type.toString(), '?', "nugget" + type.toString()));
			toRegister.add(new ShapelessOreRecipe(null, new ItemStack(GearFactory.TOGGLE_GEARS[index], 1), Blocks.LEVER, "gear" + type.toString()));
			toRegister.add(new ShapelessOreRecipe(null, new ItemStack(GearFactory.INV_TOGGLE_GEARS[index], 1), Blocks.REDSTONE_TORCH, GearFactory.TOGGLE_GEARS[index]));
			toRegister.add(new ShapelessOreRecipe(null, new ItemStack(GearFactory.TOGGLE_GEARS[index], 1), GearFactory.INV_TOGGLE_GEARS[index]));
			toRegister.add(new ShapedOreRecipe(null, new ItemStack(GearFactory.LARGE_GEARS[index], 1), "###", "#$#", "###", '#', "gear" + type.toString(), '$', "block" + type.toString()));
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
		//Reagent Tank and Reaction Chamber emptying
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.reagentTank, 1), new ItemStack(ModBlocks.reagentTank, 1, 0)));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.reactionChamber, 1), new ItemStack(ModBlocks.reactionChamber, 1, 0)));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.reagentTank, 1, 1), new ItemStack(ModBlocks.reagentTank, 1, 1)));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModBlocks.reactionChamber, 1, 1), new ItemStack(ModBlocks.reactionChamber, 1, 1)));
		//Clutches and Inverted Clutches
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModItems.clutchIron, 1), " *", "| ", '|', "stickIron", '*', "ingotTin"));
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModItems.clutchCopshowium, 1), " *", "| ", '|', "stickCopshowium", '*', "ingotTin"));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModItems.clutchInvertedIron, 1), Blocks.REDSTONE_TORCH, ModItems.clutchIron));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModItems.clutchInvertedCopshowium, 1), Blocks.REDSTONE_TORCH, ModItems.clutchCopshowium));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModItems.clutchIron, 1), ModItems.clutchInvertedIron));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(ModItems.clutchCopshowium, 1), ModItems.clutchInvertedCopshowium));
		//Wind Turbine
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.windTurbine, 1), "#*#", "*|*", "#*#", '|', "stickIron", '*', Blocks.WOOL, '#', "plankWood"));
		//Solar Heater
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(ModBlocks.solarHeater, 1), "t t", "tct", "ttt", 't', "ingotTin", 'c', "ingotCopper"));
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

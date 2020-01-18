package com.Da_Technomancer.crossroads.items.crafting;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

@Deprecated//Everything in this class should be moved to JSON
public final class ModCrafting{

	public static void init(){
		//Phial
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRItems.phialGlass, 1), "*", "*", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRItems.phialCrystal, 1), "*", "*", '*', "gemAlcCryst"));
		//Florence Flask
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRItems.florenceFlaskGlass, 1), " * ", "* *", "***", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRItems.florenceFlaskCrystal, 1), " * ", "* *", "***", '*', "gemAlcCryst"));
		//Shell
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRItems.shellGlass, 1), " * ", "* *", " * ", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRItems.shellCrystal, 1), " * ", "* *", " * ", '*', "gemAlcCryst"));
		//Alchemical Tube
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.alchemicalTubeGlass, 8), "***", "   ", "***", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.alchemicalTubeCrystal, 8), "***", "   ", "***", '*', "gemAlcCryst"));
		//Alembic
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.alembic, 1, 0), "** ", "***", "** ", '*', "ingotCopper"));
		//Chemical Vent
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.chemicalVent, 1, 0), "*#*", "###", "*#*", '*', "ingotTin", '#', Blocks.IRON_BARS));
		//Cooling Coil
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.coolingCoilGlass, 4), "* *", " * ", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.coolingCoilCrystal, 4), "* *", " * ", '*', "gemAlcCryst"));
		//Densus Plate
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.densusPlate, 6), "***", '*', "gemDensus"));
		RecipeHolder.alchemyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(CRItems.solidDensus, 1), CRBlocks.densusPlate, CRBlocks.densusPlate));
		//Anti-Densus plate
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.antiDensusPlate, 6), "***", '*', "gemAntiDensus"));
		RecipeHolder.alchemyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(CRItems.solidAntiDensus, 1), CRBlocks.antiDensusPlate, CRBlocks.antiDensusPlate));
		//Flow Limiter
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.flowLimiterGlass, 2), "*:*", '*', "blockGlass", ':', "ingotGold"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.flowLimiterCrystal, 2), "*:*", '*', "gemAlcCryst", ':', "ingotGold"));
		//Fluid Injector
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.fluidInjectorGlass, 1), "*|*", ": :", "*|*", '*', "ingotBronze", '|', CRBlocks.fluidTube, ':', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.fluidInjectorCrystal, 1), "*|*", ": :", "*|*", '*', "ingotBronze", '|', CRBlocks.fluidTube, ':', "gemAlcCryst"));
		//Glassware Holder
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.glasswareHolder, 1, 0), "^^^", "^ ^", '^', "nuggetIron"));
		//Heated Tube
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.heatedTubeGlass, 2), "*#*", '*', "blockGlass", '#', "ingotCopper"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.heatedTubeCrystal, 2), "*#*", '*', "gemAlcCryst", '#', "ingotCopper"));
		//Heat Limiter
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.heatLimiterBasic, 4, 0), "*&*", "*&*", "*#*", '*', "obsidian", '#', "dustRedstone", '&', "ingotCopper"));
		RecipeHolder.alchemyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(CRBlocks.heatLimiterRedstone, 1, 0), "dustRedstone", "dustRedstone", "dustRedstpme", CRBlocks.heatLimiterBasic));
		//Reaction Chamber
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.reactionChamberGlass, 1), "*^*", "^#^", "*^*", '*', "ingotBronze", '^', "blockGlass", '#', new ItemStack(CRBlocks.reagentTankGlass, 1)));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.reactionChamberCrystal, 1), "*^*", "^#^", "*^*", '*', "ingotBronze", '^', "gemAlcCryst", '#', new ItemStack(CRBlocks.reagentTankCrystal, 1)));
		//Reagent Pump
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.reagentPumpGlass, 1), "***", "*&*", "***", '&', "ingotBronze", '*', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.reagentPumpCrystal, 1), "***", "*&*", "***", '&', "ingotBronze", '*', "gemAlcCryst"));
		//Reagent Tank
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.reagentTankGlass, 1), "*_*", "_ _", "*_*", '*', "ingotBronze", '_', "blockGlass"));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.reagentTankCrystal, 1), "*_*", "_ _", "*_*", '*', "ingotBronze", '_', "gemAlcCryst"));
		//Redstone Alchemical Tube
		RecipeHolder.alchemyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(CRBlocks.redsAlchemicalTubeGlass, 1), "dustRedstone", "dustRedstone", "dustRedstone", new ItemStack(CRBlocks.alchemicalTubeGlass, 1)));
		RecipeHolder.alchemyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(CRBlocks.redsAlchemicalTubeCrystal, 1), "dustRedstone", "dustRedstone", "dustRedstone", new ItemStack(CRBlocks.alchemicalTubeCrystal, 1)));
		//Enhanced Tesla Coil Tops
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.teslaCoilTopDistance, 1), "TTT", "TCT", "TTT", 'T', "ingotTin", 'C', CRBlocks.teslaCoilTopNormal));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.teslaCoilTopIntensity, 1), "TTT", "TCT", "TTT", 'T', "ingotGold", 'C', CRBlocks.teslaCoilTopNormal));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.teslaCoilTopAttack, 1), "TTT", "TCT", "TTT", 'T', "ingotCopper", 'C', CRBlocks.teslaCoilTopNormal));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.teslaCoilTopEfficiency, 1), "TTT", "TCT", "TTT", 'T', "ingotBronze", 'C', CRBlocks.teslaCoilTopNormal));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.teslaCoilTopDecorative, 1), "TTT", "TCT", "TTT", 'T', "blockGlass", 'C', CRBlocks.teslaCoilTopNormal));
		//Vanadium
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRItems.vanadiumOxide, 4), "***", "*B*", "***", '*', Items.COAL, 'B', "blockCoal"));
		//Charging Stand
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.chargingStand, 1), " * ", "| |", " ^ ", '*', "ingotIron", '|', "stickIron", '^', CRBlocks.glasswareHolder));
		//Atmos Charger
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.atmosCharger, 1), "| |", "| |", "*$*", '|', "stickIron", '*', "ingotIron", '$', CRItems.leydenJar));
		//Voltus Generator
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.voltusGenerator, 1), "*C*", "M$M", "*C*", 'M', "ingotCopper", 'C', CRItems.alchCrystal, '*', "ingotIron", '$', CRItems.leydenJar));
		//Detailed Crafting Table (Cheap Alchemy Recipe)
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.detailedCrafter, 1), "*&*", "&#&", "*&*", '*', "nuggetIron", '&', "nuggetTin", '#', Blocks.CRAFTING_TABLE));
		//Tesla Ray
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRItems.teslaRay, 1), "C C", "VII", "C C", 'C', "ingotCopshowium", 'I', "ingotIron", 'V', CRItems.leydenJar));
		//Damping Powder
		RecipeHolder.alchemyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(CRItems.dampingPowder, 4), CRItems.alchemySalt, CRItems.alchemySalt, "dustSalt", "dustRedstone"));
		//Reagent Filter
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.reagentFilterGlass, 1), "IAI", "|A|", "IAI", 'I', "ingotIron", '|', "blockGlass", 'A', CRItems.lensArray));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.reagentFilterCrystal, 1), "IAI", "|A|", "IAI", 'I', "ingotIron", '|', CRItems.alchCrystal, 'A', CRItems.lensArray));

		//Flying Machine
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRItems.flyingMachine, 1), "___", "@-@", "|+|", '_', "ingotBronze", '@', "gearCopshowium", '-', new ItemStack(CRBlocks.antiDensusPlate, 1), '+', new ItemStack(CRBlocks.densusPlate, 1), '|', "stickIron"));
		//Copshowium Creation Chamber
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.copshowiumCreationChamber, 1), "*^*", "^&^", "*^*", '*', CRItems.pureQuartz, '^', CRItems.brightQuartz, '&', CRBlocks.fluidCoolingChamber));
		//Gateway Frame
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.gatewayFrame, 3), "***", "^^^", "%^%", '*', Blocks.STONE, '^', "ingotCopshowium", '%', "obsidian"));
		//Beam Cage
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRItems.beamCage, 1), " L ", "*&*", " L ", '*', CRBlocks.quartzStabilizer, '&', "ingotCopshowium", 'L', CRItems.lensArray));
		//Cage Charger
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.cageCharger, 1), " B ", "QLQ", 'B', "ingotBronze", 'Q', CRItems.pureQuartz, 'L', CRItems.brightQuartz));
		//Beam Staff
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRItems.staffTechnomancy, 1), "*C*", " | ", " | ", '*', CRItems.lensArray, 'C', CRItems.beamCage, '|', "stickIron"));
		//Modular Goggles
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRItems.moduleGoggles, 1), "***", "^&^", '&', "ingotCopshowium", '*', "ingotBronze", '^', "blockGlass"));
		//Redstone Axis
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.redstoneAxis, 1), "*^*", "^&^", "*^*", '*', "dustRedstone", '^', "nuggetBronze", '&', CRBlocks.masterAxis));
		//Master Axis (Cheap Technomancy recipe)
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.masterAxis, 1), "***", "*#*", "*&*", '*', "nuggetIron", '#', "nuggetCopshowium", '&', "stickIron"));
		//Mechanical Arm
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.mechanicalArm, 1), " *|", " | ", "*I*", 'I', "blockIron", '|', "stickIron", '*', "gearCopshowium"));
		//Redstone Registry
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.redstoneRegistry, 1), "*&*", "&^&", "*&*", '*', "nuggetTin", '&', CRBlocks.redstoneKeyboard, '^', "ingotCopshowium"));
		//Detailed Crafting Table (Cheap Technomancy Recipe)
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.detailedCrafter, 1), "*&*", "&#&", "*&*", '*', "nuggetIron", '&', "nuggetTin", '#', Blocks.CRAFTING_TABLE));
		//Clockwork Stabilizer
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.clockworkStabilizer, 1), " # ", "#*#", " # ", '*', CRBlocks.quartzStabilizer, '#', "gearCopshowium"));
		//Beacon Harness
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.beaconHarness, 1), "^&^", "&*&", "^&^", '*', "ingotCopshowium", '&', CRItems.lensArray, '^', CRItems.brightQuartz));
		//Chrono Harness
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.chronoHarness, 1), "^&^", "&*&", "^&^", '*', "ingotCopshowium", '&', "ingotIron", '^', "blockRedstone"));
		//Flux node
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.fluxNode, 1), " | ", "|C|", "I|I", 'I', "ingotIron", '|', "stickIron", 'C', "ingotCopshowium"));
		//Temporal Accelerator
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.temporalAccelerator, 1), "CCC", "Q|Q", " | ", 'C', "ingotCopshowium", '|', "stickIron", 'Q', CRItems.brightQuartz));
		//Electric Flux Stabilizer
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.fluxStabilizerElectric, 1), "RCR", "CGC", "RCR", 'C', "ingotCopshowium", 'G', "blockGlass", 'R', "dustRedstone"));
		//Beam Flux Stabilizer
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.fluxStabilizerBeam, 1), "RCR", "CGC", "RCR", 'C', "ingotCopshowium", 'G', "blockGlass", 'R', CRItems.pureQuartz));
		//Electric Flux Crystal Stabilizer
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.fluxStabilizerCrystalElectric, 1), "RCR", "CGC", "RCR", 'C', "ingotCopshowium", 'G', CRItems.alchCrystal, 'R', "dustRedstone"));
		//Beam Flux Crystal Stabilizer
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.fluxStabilizerCrystalBeam, 1), "RCR", "CGC", "RCR", 'C', "ingotCopshowium", 'G', CRItems.alchCrystal, 'R', CRItems.pureQuartz));
	}
}

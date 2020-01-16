package com.Da_Technomancer.crossroads.items.crafting;

import com.Da_Technomancer.crossroads.API.alchemy.EnumReagents;
import com.Da_Technomancer.crossroads.API.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.alchemy.Phial;
import com.Da_Technomancer.crossroads.items.itemSets.HeatCableFactory;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.items.crafting.EssentialsCrafting;
import net.minecraft.block.Blocks;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.function.Predicate;

public final class ModCrafting{

	@Deprecated
	public static final ArrayList<IRecipe> toRegister = new ArrayList<>();

	public static void init(){

		if(CRConfig.addBoboRecipes.getBoolean()){
			registerBoboItem(getFilledHopper(), "Vacuum Hopper", new ItemRecipePredicate(Blocks.HOPPER, 0), new TagCraftingStack("wool"), new ItemRecipePredicate(CrossroadsBlocks.fluidTube, 0));
			registerBoboItem(CRItems.magentaBread, "Magenta Bread", new ItemRecipePredicate(Items.BREAD, 0), new TagCraftingStack("dyeMagenta"), new TagCraftingStack("dustGlowstone"));
			registerBoboItem(CRItems.rainIdol, "Rain Idol", new TagCraftingStack("gemLapis"), new TagCraftingStack("cobblestone"), new TagCraftingStack("nuggetGold"));
			registerBoboItem(CRItems.squidHelmet, "Squid Helmet", new ItemRecipePredicate(Items.DYE, DyeColor.BLACK.getDyeDamage()), new ItemRecipePredicate(Items.FISH, 3), new TagCraftingStack("leather"));
			registerBoboItem(CRItems.pigZombieChestplate, "Zombie Pigman Chestplate", new ItemRecipePredicate(Items.BLAZE_POWDER, 0), new TagCraftingStack("leather"), new ItemRecipePredicate(Items.PORKCHOP, 0));
			registerBoboItem(CRItems.cowLeggings, "Cow Leggings", new ItemRecipePredicate(Items.MILK_BUCKET, 0), new TagCraftingStack("leather"), new ItemRecipePredicate(Items.BEEF, 0));
			registerBoboItem(CRItems.chickenBoots, "Chicken Boots", new TagCraftingStack("feather"), new TagCraftingStack("leather"), new ItemRecipePredicate(Blocks.WATERLILY, 0));
			registerBoboItem(CRItems.chaosRod, "Rod of Discord", new ItemRecipePredicate(Items.BLAZE_ROD, 0), new ItemRecipePredicate(Items.DRAGON_BREATH, 0), new ItemRecipePredicate(Items.GOLDEN_APPLE, -1));
			registerBoboItem(new ItemStack(CrossroadsBlocks.fluidVoid, 1), "Fluid Void", new ItemRecipePredicate(Blocks.SPONGE, 0), new ItemRecipePredicate(CrossroadsBlocks.fluidTube, 0), new ItemRecipePredicate(OreSetup.voidCrystal, 0));
			registerBoboItem(new ItemStack(CrossroadsBlocks.hamsterWheel, 1), "Hamster Wheel", new EdibleBlobRecipePredicate(4, 2), new ComponentCraftingStack("stick"), new TagCraftingStack("nuggetCopshowium"));
			registerBoboItem(CRItems.liechWrench, "Liechtensteinian Navy Wrench", (ItemStack s) -> EssentialsConfig.isWrench(s, false), new ItemRecipePredicate(CRItems.handCrank, 0), new ItemRecipePredicate(CRItems.staffTechnomancy, 0));
			registerBoboItem(new ItemStack(CrossroadsBlocks.maxwellDemon, 1), "Maxwell's Demon", new ItemRecipePredicate(Blocks.BEDROCK, 0), new EdibleBlobRecipePredicate(6, 4), new TagCraftingStack("ingotCopper"));
			registerBoboItem(new ItemStack(CRItems.nitroglycerin, 8), "Nitroglycerin", new TagCraftingStack("meatRaw"), new TagCraftingStack("gunpowder"), (Predicate<ItemStack>) (ItemStack stack) -> {
				if(stack.getItem() instanceof Phial){
					return CRItems.phialGlass.getReagants(stack).getQty(EnumReagents.NITRIC_ACID.id()) != 0;
				}
				return false;
			});
			registerBoboItem(new ItemStack(CRItems.poisonVodka, 1), "Poison Vodka", new ItemRecipePredicate(CRItems.solidVitriol, 0), new ItemRecipePredicate(Items.POISONOUS_POTATO, 0), (Predicate<ItemStack>) (ItemStack stack) -> stack.getItem() instanceof Phial || stack.getItem() == Items.GLASS_BOTTLE && stack.getCount() == 1);
		}
		
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
		RecipeHolder.alchemyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(CRItems.solidDensus, 1), CrossroadsBlocks.densusPlate, CrossroadsBlocks.densusPlate));
		//Anti-Densus plate
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.antiDensusPlate, 6), "***", '*', "gemAntiDensus"));
		RecipeHolder.alchemyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(CRItems.solidAntiDensus, 1), CrossroadsBlocks.antiDensusPlate, CrossroadsBlocks.antiDensusPlate));
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
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRItems.vanadiumOxide, 4), "***", "*B*", "***", '*', Items.COAL, 'B', "blockCoal"));
		//Charging Stand
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.chargingStand, 1), " * ", "| |", " ^ ", '*', "ingotIron", '|', "stickIron", '^', CrossroadsBlocks.glasswareHolder));
		//Atmos Charger
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.atmosCharger, 1), "| |", "| |", "*$*", '|', "stickIron", '*', "ingotIron", '$', CRItems.leydenJar));
		//Voltus Generator
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.voltusGenerator, 1), "*C*", "M$M", "*C*", 'M', "ingotCopper", 'C', CRItems.alchCrystal, '*', "ingotIron", '$', CRItems.leydenJar));
		//Detailed Crafting Table (Cheap Alchemy Recipe)
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.detailedCrafter, 1), "*&*", "&#&", "*&*", '*', "nuggetIron", '&', "nuggetTin", '#', Blocks.CRAFTING_TABLE));
		//Tesla Ray
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRItems.teslaRay, 1), "C C", "VII", "C C", 'C', "ingotCopshowium", 'I', "ingotIron", 'V', CRItems.leydenJar));
		//Damping Powder
		RecipeHolder.alchemyRecipes.add(new ShapelessOreRecipe(null, new ItemStack(CRItems.dampingPowder, 4), CRItems.alchemySalt, CRItems.alchemySalt, "dustSalt", "dustRedstone"));
		//Reagent Filter
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.reagentFilterGlass, 1), "IAI", "|A|", "IAI", 'I', "ingotIron", '|', "blockGlass", 'A', CRItems.lensArray));
		RecipeHolder.alchemyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.reagentFilterCrystal, 1), "IAI", "|A|", "IAI", 'I', "ingotIron", '|', CRItems.alchCrystal, 'A', CRItems.lensArray));

		//Flying Machine
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRItems.flyingMachine, 1), "___", "@-@", "|+|", '_', "ingotBronze", '@', "gearCopshowium", '-', new ItemStack(CrossroadsBlocks.antiDensusPlate, 1), '+', new ItemStack(CrossroadsBlocks.densusPlate, 1), '|', "stickIron"));
		//Copshowium Creation Chamber
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.copshowiumCreationChamber, 1), "*^*", "^&^", "*^*", '*', CRItems.pureQuartz, '^', CRItems.brightQuartz, '&', CrossroadsBlocks.fluidCoolingChamber));
		//Gateway Frame
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.gatewayFrame, 3), "***", "^^^", "%^%", '*', Blocks.STONE, '^', "ingotCopshowium", '%', "obsidian"));
		//Beam Cage
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRItems.beamCage, 1), " L ", "*&*", " L ", '*', CrossroadsBlocks.quartzStabilizer, '&', "ingotCopshowium", 'L', CRItems.lensArray));
		//Cage Charger
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.cageCharger, 1), " B ", "QLQ", 'B', "ingotBronze", 'Q', CRItems.pureQuartz, 'L', CRItems.brightQuartz));
		//Beam Staff
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRItems.staffTechnomancy, 1), "*C*", " | ", " | ", '*', CRItems.lensArray, 'C', CRItems.beamCage, '|', "stickIron"));
		//Modular Goggles
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRItems.moduleGoggles, 1), "***", "^&^", '&', "ingotCopshowium", '*', "ingotBronze", '^', "blockGlass"));
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
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRItems.pistol, 1), "CBB", "CA ", 'C', "ingotCopshowium", 'B', "ingotBronze", 'A', CRItems.lensArray));
		toRegister.add(new PrototypeItemSetRecipe(CRItems.pistol, "prot"));
		toRegister.add(new PrototypeItemClearRecipe(CRItems.pistol, "prot"));
		//Watch
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRItems.watch, 1), " * ", "*&*", " * ", '*', "ingotBronze", '&', "ingotCopshowium"));
		toRegister.add(new PrototypeItemSetRecipe(CRItems.watch, "prot"));
		toRegister.add(new PrototypeItemClearRecipe(CRItems.watch, "prot"));
		//Mechanical Arm
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.mechanicalArm, 1), " *|", " | ", "*I*", 'I', "blockIron", '|', "stickIron", '*', "gearCopshowium"));
		//Redstone Registry
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.redstoneRegistry, 1), "*&*", "&^&", "*&*", '*', "nuggetTin", '&', CrossroadsBlocks.redstoneKeyboard, '^', "ingotCopshowium"));
		//Detailed Crafting Table (Cheap Technomancy Recipe)
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.detailedCrafter, 1), "*&*", "&#&", "*&*", '*', "nuggetIron", '&', "nuggetTin", '#', Blocks.CRAFTING_TABLE));
		//Clockwork Stabilizer
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.clockworkStabilizer, 1), " # ", "#*#", " # ", '*', CrossroadsBlocks.quartzStabilizer, '#', "gearCopshowium"));
		//Beacon Harness
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.beaconHarness, 1), "^&^", "&*&", "^&^", '*', "ingotCopshowium", '&', CRItems.lensArray, '^', CRItems.brightQuartz));
		//Chrono Harness
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.chronoHarness, 1), "^&^", "&*&", "^&^", '*', "ingotCopshowium", '&', "ingotIron", '^', "blockRedstone"));
		//Flux node
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.fluxNode, 1), " | ", "|C|", "I|I", 'I', "ingotIron", '|', "stickIron", 'C', "ingotCopshowium"));
		//Temporal Accelerator
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.temporalAccelerator, 1), "CCC", "Q|Q", " | ", 'C', "ingotCopshowium", '|', "stickIron", 'Q', CRItems.brightQuartz));
		//Electric Flux Stabilizer
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.fluxStabilizerElectric, 1), "RCR", "CGC", "RCR", 'C', "ingotCopshowium", 'G', "blockGlass", 'R', "dustRedstone"));
		//Beam Flux Stabilizer
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.fluxStabilizerBeam, 1), "RCR", "CGC", "RCR", 'C', "ingotCopshowium", 'G', "blockGlass", 'R', CRItems.pureQuartz));
		//Electric Flux Crystal Stabilizer
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.fluxStabilizerCrystalElectric, 1), "RCR", "CGC", "RCR", 'C', "ingotCopshowium", 'G', CRItems.alchCrystal, 'R', "dustRedstone"));
		//Beam Flux Crystal Stabilizer
		RecipeHolder.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.fluxStabilizerCrystalBeam, 1), "RCR", "CGC", "RCR", 'C', "ingotCopshowium", 'G', CRItems.alchCrystal, 'R', CRItems.pureQuartz));

		// Heat Cable
		for(HeatInsulators insul : HeatInsulators.values()){
			toRegister.add(new ShapedOreRecipe(null, new ItemStack(HeatCableFactory.HEAT_CABLES.get(insul), 4), "###", "???", "###", '#', insul.getItem(), '?', "ingotCopper"));
			toRegister.add(new ShapelessOreRecipe(null, new ItemStack(HeatCableFactory.REDSTONE_HEAT_CABLES.get(insul), 1), "dustRedstone", "dustRedstone", "dustRedstone", HeatCableFactory.HEAT_CABLES.get(insul)));
		}

		//Lens Frame
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.lensFrame, 1), "***", "*&*", "***", '*', "stone", '&', CRItems.pureQuartz));
		//Beam Redirector
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.beamRedirector, 1), "LRL", "***", "LRL", '*', CRItems.pureQuartz, 'L', CRItems.brightQuartz, 'R', "dustRedstone"));
		//Beam Siphon
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.beamSiphon, 1), "L L", "*A*", "L L", '*', CRItems.pureQuartz, 'L', CRItems.brightQuartz, 'A', CRItems.lensArray));
		//Beam Splitter
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.beamSplitter, 1), "LRL", "*A*", "LRL", '*', CRItems.pureQuartz, 'L', CRItems.brightQuartz, 'A', CRItems.lensArray, 'R', "dustRedstone"));
		//Color Chart
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.colorChart, 1), "RGB", "^^^", "___", '_', "slabWood", '^', "paper", 'R', "dyeRed", 'G', "dyeLime", 'B', "dyeBlue"));
		//Light Cluster
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(CrossroadsBlocks.lightCluster, 8), CRItems.brightQuartz));
		//Crystalline Master Axis
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.crystalMasterAxis, 1), "*&*", "*#*", "***", '*', CRItems.pureQuartz, '#', CrossroadsBlocks.masterAxis, '&', CRItems.lensArray));
		//Fat Feeder
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.fatFeeder, 1), "***", "#A#", "***", '*', "ingotTin", '#', "netherrack", 'A', Items.GOLDEN_APPLE));
		//Detailed Crafter
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.detailedCrafter, 1), "*^*", "^&^", "*^*", '*', "ingotIron", '^', "ingotTin", '&', Blocks.CRAFTING_TABLE));
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
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.permeableGlass, 4), " G ", "G*G", " G ", 'G', "blockGlass", '*', CRItems.pureQuartz));
		//Permeable Quartz
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.permeableQuartz, 4), " G ", "G*G", " G ", 'G', CrossroadsBlocks.blockPureQuartz, '*', "blockGlass"));
		//Redstone Transmitter
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.redstoneTransmitter, 1), "QRQ", "RTR", "QRQ", 'Q', CRItems.brightQuartz, 'R', "dustRedstone", 'T', Blocks.REDSTONE_TORCH));
		//Redstone Receiver
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.redstoneReceiver, 1), "QRQ", "RTR", "QRQ", 'Q', CRItems.brightQuartz, 'R', "dustRedstone", 'T', "blockRedstone"));
		//Dynamo
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.dynamo, 1), "-@-", "===", '@', "gearCopper", '-', "stickIron", '=', "ingotIron"));
		//Cavorite Block
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CrossroadsBlocks.cavorite, 2), "**", "**", '*', "gemCavorite"));
		toRegister.add(new ShapelessOreRecipe(null, new ItemStack(CRItems.solidCavorite, 2), CrossroadsBlocks.cavorite));
		//Leyden Jar
		toRegister.add(new ShapedOreRecipe(null, new ItemStack(CRItems.leydenJar, 1), " | ", "*r*", "***", '|', "stickIron", 'r', "dustRedstone", '*', "nuggetIron"));
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
		nbttagcompound.putByte("Slot", (byte) 0);
		new ItemStack(CRItems.vacuum).writeToNBT(nbttagcompound);
		nbttag.appendTag(nbttagcompound);
		nbt.put("Items", nbttag);
		stack.setTagInfo("BlockEntityTag", nbt);

		CompoundNBT nbttagcompound1 = new CompoundNBT();
		ListNBT nbttaglist = new ListNBT();
		nbttaglist.appendTag(new StringNBT("(+NBT)"));
		nbttagcompound1.put("Lore", nbttaglist);
		stack.setTagInfo("display", nbttagcompound1);
		stack.setStackDisplayName("Vacuum Hopper");
		return stack;
	}

	private static void registerBoboItem(Item item, String configName, Predicate<ItemStack> ingr1, Predicate<ItemStack> ingr2, Predicate<ItemStack> ingr3){
		registerBoboItem(new ItemStack(item, 1), configName, ingr1, ingr2, ingr3);
	}

	private static void registerBoboItem(ItemStack item, String configName, Predicate<ItemStack> ingr1, Predicate<ItemStack> ingr2, Predicate<ItemStack> ingr3){
		Property prop = CRConfig.config.get(CRConfig.CAT_BOBO, configName + " bobo-item recipe", true, "Default: true");
		CRConfig.boboItemProperties.add(prop);
		if(((ForgeConfigSpec.BooleanValue) prop).get()){
			EssentialsCrafting.brazierBoboRecipes.add(Pair.of(new Predicate[] {ingr1, ingr2, ingr3}, item));
		}
	}
}

package com.Da_Technomancer.crossroads.items.crafting;

import com.Da_Technomancer.crossroads.API.alchemy.EnumReagents;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.alchemy.Phial;
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
			registerBoboItem(getFilledHopper(), "Vacuum Hopper", new ItemRecipePredicate(Blocks.HOPPER, 0), new TagCraftingStack("wool"), new ItemRecipePredicate(CRBlocks.fluidTube, 0));
			registerBoboItem(CRItems.magentaBread, "Magenta Bread", new ItemRecipePredicate(Items.BREAD, 0), new TagCraftingStack("dyeMagenta"), new TagCraftingStack("dustGlowstone"));
			registerBoboItem(CRItems.rainIdol, "Rain Idol", new TagCraftingStack("gemLapis"), new TagCraftingStack("cobblestone"), new TagCraftingStack("nuggetGold"));
			registerBoboItem(CRItems.squidHelmet, "Squid Helmet", new ItemRecipePredicate(Items.DYE, DyeColor.BLACK.getDyeDamage()), new ItemRecipePredicate(Items.FISH, 3), new TagCraftingStack("leather"));
			registerBoboItem(CRItems.pigZombieChestplate, "Zombie Pigman Chestplate", new ItemRecipePredicate(Items.BLAZE_POWDER, 0), new TagCraftingStack("leather"), new ItemRecipePredicate(Items.PORKCHOP, 0));
			registerBoboItem(CRItems.cowLeggings, "Cow Leggings", new ItemRecipePredicate(Items.MILK_BUCKET, 0), new TagCraftingStack("leather"), new ItemRecipePredicate(Items.BEEF, 0));
			registerBoboItem(CRItems.chickenBoots, "Chicken Boots", new TagCraftingStack("feather"), new TagCraftingStack("leather"), new ItemRecipePredicate(Blocks.WATERLILY, 0));
			registerBoboItem(CRItems.chaosRod, "Rod of Discord", new ItemRecipePredicate(Items.BLAZE_ROD, 0), new ItemRecipePredicate(Items.DRAGON_BREATH, 0), new ItemRecipePredicate(Items.GOLDEN_APPLE, -1));
			registerBoboItem(new ItemStack(CRBlocks.fluidVoid, 1), "Fluid Void", new ItemRecipePredicate(Blocks.SPONGE, 0), new ItemRecipePredicate(CRBlocks.fluidTube, 0), new ItemRecipePredicate(OreSetup.voidCrystal, 0));
			registerBoboItem(new ItemStack(CRBlocks.hamsterWheel, 1), "Hamster Wheel", new EdibleBlobRecipePredicate(4, 2), new ComponentCraftingStack("stick"), new TagCraftingStack("nuggetCopshowium"));
			registerBoboItem(CRItems.liechWrench, "Liechtensteinian Navy Wrench", (ItemStack s) -> EssentialsConfig.isWrench(s, false), new ItemRecipePredicate(CRItems.handCrank, 0), new ItemRecipePredicate(CRItems.staffTechnomancy, 0));
			registerBoboItem(new ItemStack(CRBlocks.maxwellDemon, 1), "Maxwell's Demon", new ItemRecipePredicate(Blocks.BEDROCK, 0), new EdibleBlobRecipePredicate(6, 4), new TagCraftingStack("ingotCopper"));
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

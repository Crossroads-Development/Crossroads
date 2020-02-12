package com.Da_Technomancer.crossroads.items.crafting;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;

@Deprecated//Everything in this class should be moved to JSON
public final class ModCrafting{

	public static void init(){
		//Copshowium Creation Chamber
		CRRecipes.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.copshowiumCreationChamber, 1), "*^*", "^&^", "*^*", '*', CRItems.pureQuartz, '^', CRItems.brightQuartz, '&', CRBlocks.fluidCoolingChamber));
		//Gateway Frame
		CRRecipes.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.gatewayFrame, 3), "***", "^^^", "%^%", '*', Blocks.STONE, '^', "ingotCopshowium", '%', "obsidian"));
		//Beam Cage
		CRRecipes.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRItems.beamCage, 1), " L ", "*&*", " L ", '*', CRBlocks.quartzStabilizer, '&', "ingotCopshowium", 'L', CRItems.lensArray));
		//Cage Charger
		CRRecipes.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.cageCharger, 1), " B ", "QLQ", 'B', "ingotBronze", 'Q', CRItems.pureQuartz, 'L', CRItems.brightQuartz));
		//Beam Staff
		CRRecipes.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRItems.staffTechnomancy, 1), "*C*", " | ", " | ", '*', CRItems.lensArray, 'C', CRItems.beamCage, '|', "stickIron"));
		//Modular Goggles
		CRRecipes.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRItems.moduleGoggles, 1), "***", "^&^", '&', "ingotCopshowium", '*', "ingotBronze", '^', "blockGlass"));
		//Redstone Axis
		CRRecipes.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.redstoneAxis, 1), "*^*", "^&^", "*^*", '*', "dustRedstone", '^', "nuggetBronze", '&', CRBlocks.masterAxis));
		//Master Axis (Cheap Technomancy recipe)
		CRRecipes.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.masterAxis, 1), "***", "*#*", "*&*", '*', "nuggetIron", '#', "nuggetCopshowium", '&', "stickIron"));
		//Clockwork Stabilizer
		CRRecipes.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.clockworkStabilizer, 1), " # ", "#*#", " # ", '*', CRBlocks.quartzStabilizer, '#', "gearCopshowium"));
		//Beacon Harness
		CRRecipes.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.beaconHarness, 1), "^&^", "&*&", "^&^", '*', "ingotCopshowium", '&', CRItems.lensArray, '^', CRItems.brightQuartz));
		//Chrono Harness
		CRRecipes.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.chronoHarness, 1), "^&^", "&*&", "^&^", '*', "ingotCopshowium", '&', "ingotIron", '^', "blockRedstone"));
		//Flux node
		CRRecipes.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.fluxNode, 1), " | ", "|C|", "I|I", 'I', "ingotIron", '|', "stickIron", 'C', "ingotCopshowium"));
		//Temporal Accelerator
		CRRecipes.technomancyRecipes.add(new ShapedOreRecipe(null, new ItemStack(CRBlocks.temporalAccelerator, 1), "CCC", "Q|Q", " | ", 'C', "ingotCopshowium", '|', "stickIron", 'Q', CRItems.brightQuartz));
		//TODO flux sink
	}
}

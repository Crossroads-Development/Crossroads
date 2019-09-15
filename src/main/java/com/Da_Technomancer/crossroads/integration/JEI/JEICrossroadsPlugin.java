package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.gui.container.DetailedCrafterContainer;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Map.Entry;

@JeiPlugin
@SuppressWarnings("unused")
public class JEICrossroadsPlugin implements IModPlugin{

	private static final ResourceLocation PLUGIN_ID = new ResourceLocation(Crossroads.MODID, "jei_plugin");

	@Override
	public ResourceLocation getPluginUid(){
		return PLUGIN_ID;
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry){
		registry.addRecipeCatalyst(new ItemStack(CrossroadsBlocks.millstone, 1), MillstoneCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CrossroadsBlocks.fluidCoolingChamber, 1), FluidCoolingCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CrossroadsBlocks.heatingCrucible, 1), HeatingCrucibleCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CrossroadsBlocks.detailedCrafter, 1), DetailedCrafterCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CrossroadsBlocks.beamExtractor, 1), BeamExtractorCategory.ID);
//TODO		registry.addRecipeCatalyst(new ItemStack(CrossroadsBlocks.beamReflector, 1), FusionBeamCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CrossroadsBlocks.reactionChamberGlass, 1), ReactionCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CrossroadsBlocks.reactionChamberCrystal, 1), ReactionCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CrossroadsBlocks.glasswareHolder, 1), ReactionCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CrossroadsBlocks.stampMill, 1), StampMillCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CrossroadsBlocks.oreCleanser, 1), OreCleanserCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(CrossroadsBlocks.blastFurnace, 1), BlastFurnaceCategory.ID);
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration){
		registration.addRecipeTransferHandler(DetailedCrafterContainer.class, DetailedCrafterCategory.ID, 0, 9, 10, 36);
		registration.addRecipeTransferHandler(DetailedCrafterContainer.class, VanillaRecipeCategoryUid.CRAFTING, 0, 9, 10, 36);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration){
		for(Entry<ResourceLocation, ArrayList<Object>> recipeGroup : RecipeHolder.JEIWrappers.entrySet()){
			registration.addRecipes(recipeGroup.getValue(), recipeGroup.getKey());
		}
	}

	@Override
	public void registerIngredients(IModIngredientRegistration registry){
		ReagIngr.populate();
		registry.register(ReagIngr.REAG, ReagIngr.REAG_TYPES, new ReagentIngredientHelper(), ReagentIngredientRenderer.RENDERER);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry){
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(new MillstoneCategory(guiHelper), new FluidCoolingCategory(guiHelper), new HeatingCrucibleCategory(guiHelper), new DetailedCrafterCategory(guiHelper), new BeamExtractorCategory(guiHelper), new ReactionCategory(guiHelper), new ReagInfoCategory(guiHelper), new StampMillCategory(guiHelper), new OreCleanserCategory(guiHelper), new BlastFurnaceCategory(guiHelper));
	}
}

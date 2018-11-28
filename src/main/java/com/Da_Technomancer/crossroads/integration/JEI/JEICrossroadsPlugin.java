package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.gui.container.DetailedCrafterContainer;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Map.Entry;

@JEIPlugin
public class JEICrossroadsPlugin implements IModPlugin{

	@Override
	public void register(@Nonnull IModRegistry registry){

		registry.addRecipeCatalyst(new ItemStack(ModBlocks.millstone, 1), MillstoneCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(ModBlocks.fluidCoolingChamber, 1), FluidCoolingCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(ModBlocks.heatingCrucible, 1), HeatingCrucibleCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(ModBlocks.detailedCrafter, 1), DetailedCrafterCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(ModBlocks.beamExtractor, 1), BeamExtractorCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(ModBlocks.beamReflector, 1), FusionBeamCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(ModBlocks.reactionChamberGlass, 1), ReactionCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(ModBlocks.reactionChamberCrystal, 1), ReactionCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(ModBlocks.glasswareHolder, 1), ReactionCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(ModBlocks.stampMill, 1), StampMillCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(ModBlocks.oreCleanser, 1), OreCleanserCategory.ID);
		registry.addRecipeCatalyst(new ItemStack(ModBlocks.blastFurnace, 1), BlastFurnaceCategory.ID);

		for(Entry<String, ArrayList<IRecipeWrapper>> recipeGroup : RecipeHolder.JEIWrappers.entrySet()){
			registry.addRecipes(recipeGroup.getValue(), recipeGroup.getKey());
		}
		
		registry.getRecipeTransferRegistry().addRecipeTransferHandler(DetailedCrafterContainer.class, DetailedCrafterCategory.ID, 0, 9, 10, 36);
		registry.getRecipeTransferRegistry().addRecipeTransferHandler(DetailedCrafterContainer.class, VanillaRecipeCategoryUid.CRAFTING, 0, 9, 10, 36);
	}

	@Override
	public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime){

	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry){
		
	}

	@Override
	public void registerIngredients(IModIngredientRegistration registry){
		registry.register(ReagIngr.class, ReagIngr.REAG_TYPES, new ReagentIngredientHelper(), ReagentIngredientRenderer.RENDERER);		
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry){
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(new MillstoneCategory(guiHelper), new FluidCoolingCategory(guiHelper), new HeatingCrucibleCategory(guiHelper), new DetailedCrafterCategory(guiHelper), new BeamExtractorCategory(guiHelper), new FusionBeamCategory(guiHelper), new ReactionCategory(guiHelper), new ReagInfoCategory(guiHelper), new StampMillCategory(guiHelper), new OreCleanserCategory(guiHelper), new BlastFurnaceCategory(guiHelper));
	}
}

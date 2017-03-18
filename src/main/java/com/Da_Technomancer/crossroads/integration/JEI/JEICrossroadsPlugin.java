package com.Da_Technomancer.crossroads.integration.JEI;

import javax.annotation.Nonnull;

import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class JEICrossroadsPlugin implements IModPlugin{

	@Override
	public void register(@Nonnull IModRegistry registry){

		registry.addRecipeCategories(new GrindstoneCategory(registry.getJeiHelpers().getGuiHelper()), new FluidCoolingCategory(registry.getJeiHelpers().getGuiHelper()), new HeatingCrucibleCategory(registry.getJeiHelpers().getGuiHelper()), new HeatExchangerCategory(registry.getJeiHelpers().getGuiHelper()), new DetailedCrafterCategory(registry.getJeiHelpers().getGuiHelper()));
		registry.addRecipeHandlers(new GrindstoneRecipeHandler(), new FluidCoolingRecipeHandler(), new HeatingCrucibleRecipeHandler(), new HeatExchangerRecipeHandler(), new DetailedCrafterRecipeHandler());

		registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.grindstone, 1), GrindstoneCategory.id);
		registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.fluidCoolingChamber, 1), FluidCoolingCategory.id);
		registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.heatingCrucible, 1), HeatingCrucibleCategory.id);
		registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.heatExchanger, 1), HeatExchangerCategory.ID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.insulHeatExchanger, 1), HeatExchangerCategory.ID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.detailedCrafter, 1), DetailedCrafterCategory.ID);
		
		registry.addRecipes(RecipeHolder.JEIWrappers);
	}

	@Override
	public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime){

	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry){
		
	}

	@Override
	public void registerIngredients(IModIngredientRegistration registry){
		
	}
}

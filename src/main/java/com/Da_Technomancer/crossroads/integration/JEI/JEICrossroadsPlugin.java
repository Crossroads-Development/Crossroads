package com.Da_Technomancer.crossroads.integration.JEI;

import javax.annotation.Nonnull;

import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class JEICrossroadsPlugin implements IModPlugin{

	@Override
	public void register(@Nonnull IModRegistry registry){
		
		registry.addRecipeCategories(new GrindstoneCategory(registry.getJeiHelpers().getGuiHelper()), new FluidCoolingCategory(registry.getJeiHelpers().getGuiHelper()), new HeatingCrucibleCategory(registry.getJeiHelpers().getGuiHelper()));
		registry.addRecipeHandlers(new GrindstoneRecipeHandler(), new FluidCoolingRecipeHandler(), new HeatingCrucibleRecipeHandler());
		
		registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.grindstone, 1), GrindstoneCategory.id);
		registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.fluidCoolingChamber, 1), FluidCoolingCategory.id);
		registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.heatingCrucible, 1), HeatingCrucibleCategory.id);
		
		registry.addRecipes(RecipeHolder.JEIWrappers);
	}

	@Override
	public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime){
		
	}

}

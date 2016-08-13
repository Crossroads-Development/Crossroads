package com.Da_Technomancer.crossroads.client.integration.JEI;

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
		
		registry.addRecipeCategories(new GrindstoneCategory(registry.getJeiHelpers().getGuiHelper()));
		registry.addRecipeHandlers(new GrindstoneRecipeHandler());
		registry.addRecipes(RecipeHolder.JEIWrappers);
		
		registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.grindstone, 1), GrindstoneCategory.id);
		
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime){
		
	}

}

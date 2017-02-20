package com.Da_Technomancer.crossroads.integration.JEI;

import javax.annotation.Nonnull;

import com.Da_Technomancer.crossroads.Main;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class GrindstoneRecipeHandler implements IRecipeHandler<GrindstoneRecipe>{

	@Override
	public Class<GrindstoneRecipe> getRecipeClass(){
		return GrindstoneRecipe.class;
	}

	@Override
	public String getRecipeCategoryUid(@Nonnull GrindstoneRecipe recipe){
		return Main.MODID + ".grindstone";
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull GrindstoneRecipe recipe){
		return new GrindstoneRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(@Nonnull GrindstoneRecipe recipe){
		return true;
	}
}

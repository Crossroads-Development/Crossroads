package com.Da_Technomancer.crossroads.integration.JEI;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class DetailedCrafterRecipeHandler implements IRecipeHandler<DetailedCrafterRecipe>{

	@Override
	public Class<DetailedCrafterRecipe> getRecipeClass(){
		return DetailedCrafterRecipe.class;
	}

	@Override
	public String getRecipeCategoryUid(@Nonnull DetailedCrafterRecipe recipe){
		return DetailedCrafterCategory.ID;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull DetailedCrafterRecipe recipe){
		return new DetailedCrafterRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(@Nonnull DetailedCrafterRecipe recipe){
		return true;
	}
}

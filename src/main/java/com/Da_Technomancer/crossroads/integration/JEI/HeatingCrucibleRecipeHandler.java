package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.Main;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class HeatingCrucibleRecipeHandler implements IRecipeHandler<HeatingCrucibleRecipe>{

	@Override
	public Class<HeatingCrucibleRecipe> getRecipeClass(){
		return HeatingCrucibleRecipe.class;
	}

	@Override
	public String getRecipeCategoryUid(){
		return Main.MODID + ".heatingCrucible";
	}

	@Override
	public String getRecipeCategoryUid(HeatingCrucibleRecipe recipe){
		return getRecipeCategoryUid();
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(HeatingCrucibleRecipe recipe){
		return new HeatingCrucibleRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(HeatingCrucibleRecipe recipe){
		return true;
	}

}

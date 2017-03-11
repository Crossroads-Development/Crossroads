package com.Da_Technomancer.crossroads.integration.JEI;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class HeatExchangerRecipeHandler implements IRecipeHandler<HeatExchangerRecipe>{

	@Override
	public Class<HeatExchangerRecipe> getRecipeClass(){
		return HeatExchangerRecipe.class;
	}

	@Override
	public String getRecipeCategoryUid(HeatExchangerRecipe recipe){
		return HeatExchangerCategory.ID;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(HeatExchangerRecipe recipe){
		return new HeatExchangerRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(HeatExchangerRecipe recipe){
		return true;
	}

}

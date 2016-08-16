package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.Main;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class FluidCoolingRecipeHandler implements IRecipeHandler<FluidCoolingRecipe>{

	protected static final String id = Main.MODID + ".fluidCooling";

	@Override
	public Class<FluidCoolingRecipe> getRecipeClass(){
		return FluidCoolingRecipe.class;
	}

	@Override
	public String getRecipeCategoryUid(){
		return id;
	}

	@Override
	public String getRecipeCategoryUid(FluidCoolingRecipe recipe){
		return getRecipeCategoryUid();
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(FluidCoolingRecipe recipe){
		return new FluidCoolingRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(FluidCoolingRecipe recipe){
		return true;
	}

}

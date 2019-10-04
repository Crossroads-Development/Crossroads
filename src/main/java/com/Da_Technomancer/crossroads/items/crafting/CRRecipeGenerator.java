package com.Da_Technomancer.crossroads.items.crafting;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;

import java.util.function.Consumer;

public class CRRecipeGenerator extends RecipeProvider{

	public CRRecipeGenerator(DataGenerator gen){
		super(gen);
	}

	@Override
	public String getName(){
		return Crossroads.MODNAME + " Recipes";
	}

	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> cons){
//		super.registerRecipes(cons);
		//TODO all the recipes
	}
}

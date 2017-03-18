package com.Da_Technomancer.crossroads.integration.JEI;

import net.minecraft.item.crafting.IRecipe;

public class DetailedCrafterRecipe{

	private final IRecipe recipe;
	private final int type;

	/**
	 * 
	 * @param recipe
	 * @param type Technomancy: 0, Alchemy: NYI, Witchcraft: NYI
	 */
	public DetailedCrafterRecipe(IRecipe recipe, int type){
		this.recipe = recipe;
		this.type = type;
	}

	public int getType(){
		return type;
	}

	public IRecipe getRecipe(){
		return recipe;
	}
}

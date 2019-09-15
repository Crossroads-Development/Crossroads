package com.Da_Technomancer.crossroads.integration.JEI;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.crafting.IRecipe;

public class DetailedCrafterRecipe{

	protected final IRecipe<CraftingInventory> recipe;
	protected final int type;

	/**
	 * @param recipe The recipe
	 * @param type Technomancy: 0, Alchemy: 1, Witchcraft: NYI (2)
	 */
	public DetailedCrafterRecipe(IRecipe<CraftingInventory> recipe, int type){
		this.recipe = recipe;
		this.type = type;
	}
}

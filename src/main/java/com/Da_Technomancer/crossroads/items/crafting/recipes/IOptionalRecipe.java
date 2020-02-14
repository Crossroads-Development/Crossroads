package com.Da_Technomancer.crossroads.items.crafting.recipes;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

public interface IOptionalRecipe<T extends IInventory> extends IRecipe<T>{

	@Override
	default ItemStack getCraftingResult(T inv){
		return getRecipeOutput().copy();
	}

	/**
	 * Whether this recipe should be considered "real". If not, ignore it.
	 * This exists to help players disable recipes with data packs- the vanilla method is to set output to air
	 * @return Whether this recipe is active
	 */
	boolean isEnabled();
}

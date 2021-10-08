package com.Da_Technomancer.crossroads.crafting.recipes;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public interface IOptionalRecipe<T extends Container> extends Recipe<T>{

	@Override
	default ItemStack assemble(T inv){
		return getResultItem().copy();
	}

	/**
	 * Whether this recipe should be considered "real". If not, ignore it.
	 * This exists to help players disable recipes with data packs- the vanilla method is to set output to air
	 * @return Whether this recipe is active
	 */
	boolean isEnabled();
}

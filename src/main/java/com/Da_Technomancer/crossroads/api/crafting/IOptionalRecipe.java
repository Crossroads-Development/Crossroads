package com.Da_Technomancer.crossroads.api.crafting;

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

	/**
	 * Whether this recipe is either a custom recipe category or has special behavior
	 * Disables recipe book support; however the recipe book doesn't work for custom recipe categories anyway
	 * If this is false and a custom recipe, the recipe book logs errors at startup
	 * @return Whether this recipe is either a custom recipe category or has special behavior
	 */
	@Override
	default boolean isSpecial(){
		return true;
	}
}

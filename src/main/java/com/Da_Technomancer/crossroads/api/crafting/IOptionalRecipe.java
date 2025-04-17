package com.Da_Technomancer.crossroads.api.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface IOptionalRecipe<T extends RecipeInput> extends Recipe<T>{

	// TODO: Determine if changed inherited param "level" should remain unused
	@Override
	default @NotNull ItemStack assemble(T recipeInput, HolderLookup.Provider var2){
		return assemble(recipeInput);
	}

	/**
	 * Gets the created itemstack
	 * Safe to modify.
	 * Some recipes may return different items for assemble vs getResultItem- trust assemble
	 */
	default ItemStack assemble(T recipeInput){
		ItemStack result = getResultItem();
		if(result.isEmpty()){
			return result;
		}else{
			return result.copy();
		}
	}

	// TODO: Determine if changed inherited param "provider" should remain unused
	@Override
	default @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider provider){
		return getResultItem();
	}

	/**
	 * Gets the created itemstack
	 * DO NOT MODIFY THE RETURNED ITEMSTACK
	 * Some recipes may return different items for assemble vs getResultItem- trust assemble
	 */
	ItemStack getResultItem();

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

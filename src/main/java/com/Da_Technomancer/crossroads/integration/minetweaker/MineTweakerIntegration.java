package com.Da_Technomancer.crossroads.integration.minetweaker;

import java.util.ArrayList;
import java.util.Arrays;

import com.Da_Technomancer.crossroads.integration.ModIntegration;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.item.ItemStack;

/**
 * Provide MineTweaker integration for the mod.
 */
public class MineTweakerIntegration {

	public static void init() {
		MineTweakerAPI.registerClass(GrindstoneHandler.class);
		MineTweakerAPI.registerClass(FluidCoolingChamberHandler.class);
	}

	private static final ItemStack[] EMPTY = new ItemStack[0];
	
	public static ItemStack[] toItemStack(IIngredient... ingredients) {
		if (ingredients == null || ingredients.length == 0) {
			return EMPTY;
		}
		
		ArrayList<IIngredient> ingred = new ArrayList<IIngredient>(Arrays.asList(ingredients));
		ingred.remove(null);
		ingred.remove(null);
		
		if(ingred.size() == 0){
			return EMPTY;
		}
		
		ItemStack[] itemStacks = new ItemStack[ingred.size()];
		for (int i = 0; i < ingred.size(); i++) {
			itemStacks[i] = MineTweakerMC.getItemStack(ingred.get(i));
		}
		return itemStacks;
	}

	public static void refreshJEI() {
		if (ModIntegration.isJEIAvailable) {
			RecipeHolder.JEIWrappers.clear();
			RecipeHolder.rebind();
		}
	}
}

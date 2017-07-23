package com.Da_Technomancer.crossroads.integration.crafttweaker;

import java.util.ArrayList;
import java.util.Arrays;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;

/** Provide CraftTweaker integration for the mod. */
public class CraftTweakerIntegration{

	public static void init(){
		CraftTweakerAPI.registerClass(GrindstoneHandler.class);
		CraftTweakerAPI.registerClass(FluidCoolingChamberHandler.class);
	}

	private static final ItemStack[] EMPTY = new ItemStack[0];

	protected static ItemStack[] toItemStack(IIngredient... ingredients){
		if(ingredients == null || ingredients.length == 0){
			return EMPTY;
		}

		ArrayList<IIngredient> ingred = new ArrayList<IIngredient>(Arrays.asList(ingredients));
		ingred.remove(null);
		ingred.remove(null);

		if(ingred.size() == 0){
			return EMPTY;
		}

		ItemStack[] itemStacks = new ItemStack[ingred.size()];
		for(int i = 0; i < ingred.size(); i++){
			itemStacks[i] = CraftTweakerMC.getItemStack(ingred.get(i));
		}
		return itemStacks;
	}
}

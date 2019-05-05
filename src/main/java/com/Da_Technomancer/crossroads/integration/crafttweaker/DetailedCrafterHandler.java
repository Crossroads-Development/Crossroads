package com.Da_Technomancer.crossroads.integration.crafttweaker;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;

/**
 * Integration for the Detailed Crafter.
 */
@ZenClass("mods.crossroads.DetailedCrafter")
public class DetailedCrafterHandler{

	/** Removes all Detailed Crafter technomancy recipes that make the given item
	 *
	 * @param output The item the recipe(s) previously made
	 */
	@ZenMethod
	public static void removeTechnoRecipe(IItemStack output){
		ItemStack out = CraftTweakerMC.getItemStack(output);
		if(out.isEmpty()){
			return;
		}

		CraftTweakerAPI.apply(new Remove(out.getItem(), out.getMetadata(), RecipeHolder.technomancyRecipes));
	}

	/** Removes all Detailed Crafter alchemy recipes that make the given item
	 *
	 * @param output The item the recipe(s) previously made
	 */
	@ZenMethod
	public static void removeAlcRecipe(IItemStack output){
		ItemStack out = CraftTweakerMC.getItemStack(output);
		if(out.isEmpty()){
			return;
		}

		CraftTweakerAPI.apply(new Remove(out.getItem(), out.getMetadata(), RecipeHolder.alchemyRecipes));
	}

	/**
	 * Operation to remove Detailed Crafter recipes with a given output
	 */
	private static class Remove implements IAction{

		private final Item item;
		private final int meta;
		private final ArrayList<IRecipe> recipeList;

		private Remove(Item item, int meta, ArrayList<IRecipe> recipeList){
			this.item = item;
			this.meta = meta;
			this.recipeList = recipeList;
		}

		@Override
		public void apply(){
			for(int i = 0; i < recipeList.size(); i++){
				ItemStack output = recipeList.get(i).getRecipeOutput();
				if(output.getItem() == item && output.getMetadata() == meta){
					Main.logger.info("Removing Detailed Crafter recipe: " + item + ": " + recipeList.remove(i--));
				}
			}
		}

		@Override
		public String describe(){
			return "Removing Detailed Crafter " + (recipeList == RecipeHolder.alchemyRecipes ? "Alchemy" : "Technomancy") + " recipe for " + item.getRegistryName();
		}
	}
}

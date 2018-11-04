package com.Da_Technomancer.crossroads.integration.crafttweaker;

import com.Da_Technomancer.crossroads.items.crafting.ItemRecipePredicate;
import com.Da_Technomancer.crossroads.items.crafting.OreDictCraftingStack;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import com.Da_Technomancer.crossroads.items.crafting.RecipePredicate;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * Integration for the Industrial Crucible. All recipes activate at 1200°C.
 */
@ZenClass("mods.crossroads.IndustrialCrucible")
public class HeatingCrucibleHandler{

	/**
	 * Adds a recipe from an ItemStack to a FluidStack to be crafted in a Heating Crucible. If a recipe with the same input already exists, this does not remove the other recipe. Call removeRecipe first in that case. 
	 * @param input The ItemStack input. Stacksize is ignored. 
	 * @param out The FluidStack created.
	 */
	@ZenMethod
	public static void addRecipe(IItemStack input, ILiquidStack out){
		ItemStack in = CraftTweakerMC.getItemStack(input);
		CraftTweakerAPI.apply(new Add(new ItemRecipePredicate(in.getItem(), in.getMetadata()), CraftTweakerMC.getLiquidStack(out)));
	}
	
	/**
	 * Adds a recipe from an OreDict to a FluidStack to be crafted in a Heating Crucible. If a recipe with the same input already exists, this does not remove the other recipe. Call removeRecipe first in that case. 
	 * @param input The OreDict input. Stacksize is ignored. 
	 * @param out The FluidStack created. 
	 */
	@ZenMethod
	public static void addRecipe(IOreDictEntry input, ILiquidStack out){
		CraftTweakerAPI.apply(new Add(new OreDictCraftingStack(input.getName()), CraftTweakerMC.getLiquidStack(out)));
	}
	
	/**
	 * This method removes a recipe from an ItemStack to a FluidStack in a Heating Crucible. 
	 * @param input The input itemstack (stacksize is ignored)
	 */
	@ZenMethod
	public static void removeRecipe(IItemStack input){
		ItemStack in = CraftTweakerMC.getItemStack(input);
		CraftTweakerAPI.apply(new Remove(new ItemRecipePredicate(in.getItem(), in.getMetadata())));
	}
	
	/**
	 * This method removes a recipe from an OreDict to a FluidStack in a Heating Crucible. 
	 * @param input The input oredict (stacksize is ignored)
	 */
	@ZenMethod
	public static void removeRecipe(IOreDictEntry input){
		CraftTweakerAPI.apply(new Remove(new OreDictCraftingStack(input.getName())));
	}
	
	private static class Add implements IAction{

		private final RecipePredicate<ItemStack> in;
		private final FluidStack out;
		
		private Add(RecipePredicate<ItemStack> input, FluidStack out){
			this.in = input;
			this.out = out;
		}
		
		@Override
		public void apply(){
			RecipeHolder.crucibleRecipes.put(in, out);
		}

		@Override
		public String describe(){
			return "Adding Crucible recipe for " + in.toString();
		}	
	}
	
	private static class Remove implements IAction{

		private final RecipePredicate<ItemStack> input;
		
		private Remove(RecipePredicate<ItemStack> input){
			this.input = input;
		}
		
		@Override
		public void apply(){
			RecipeHolder.crucibleRecipes.remove(input);
		}

		@Override
		public String describe(){
			return "Removing Crucible recipe for " + input.toString();
		}
	}
}

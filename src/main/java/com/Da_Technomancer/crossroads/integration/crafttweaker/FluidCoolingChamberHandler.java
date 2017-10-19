package com.Da_Technomancer.crossroads.integration.crafttweaker;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/** Integration for the Fluid Cooling Chamber */
@ZenClass("mods.crossroads.FluidCoolingChamber")
public class FluidCoolingChamberHandler{

	/** Add a new fluid cooling chamber recipe that produces the output stack from the input liquid.
	 *
	 * @param output
	 *            the result of the recipe
	 * @param input
	 *            the input liquid and amount that is required to produce the result
	 * @param maxTemp
	 *            the maximum temperature allowed
	 * @param heatAdded
	 *            the amount of heat that is added */
	@ZenMethod
	public static void addRecipe(IItemStack output, ILiquidStack input, double maxTemp, double heatAdded){
		FluidStack liquidStack = CraftTweakerMC.getLiquidStack(input);
		if(liquidStack == null){
			return;
		}
		CraftTweakerAPI.apply(new Add(liquidStack.getFluid(), liquidStack.amount, CraftTweakerMC.getItemStack(output), maxTemp, heatAdded));
	}

	/** Operation to add a recipe. */
	private static class Add implements IAction{
		private final Fluid fluid;
		private final Pair<Integer, Triple<ItemStack, Double, Double>> recipe;

		private Add(Fluid fluid, Integer amount, ItemStack output, Double maxTemp, Double heatAdded){
			this.fluid = fluid;
			this.recipe = Pair.of(amount, Triple.of(output, maxTemp, heatAdded));
		}

		@Override
		public void apply(){
			RecipeHolder.fluidCoolingRecipes.put(fluid, recipe);
		}

		@Override
		public String describe(){
			return "Adding fluid cooling recipe for " + fluid.getName();
		}
	}

	/** Remove the cooling chamber recipe for the input liquid.
	 *
	 * @param input
	 *            the input liquid of the recipe */
	@ZenMethod
	public static void removeRecipe(ILiquidStack input){
		FluidStack liquidStack = CraftTweakerMC.getLiquidStack(input);
		if(liquidStack != null){
			CraftTweakerAPI.apply(new Remove(liquidStack.getFluid()));
		}
	}

	/** Operation to remove a recipe. */
	private static class Remove implements IAction{
		Fluid fluid;

		Remove(Fluid fluid){
			this.fluid = fluid;
		}

		@Override
		public void apply(){
			RecipeHolder.fluidCoolingRecipes.remove(fluid);
		}

		@Override
		public String describe(){
			return "Removing fluid cooling recipe for " + fluid.getName();
		}
	}
}

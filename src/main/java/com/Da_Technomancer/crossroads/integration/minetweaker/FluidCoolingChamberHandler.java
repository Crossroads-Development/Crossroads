package com.Da_Technomancer.crossroads.integration.minetweaker;

import com.Da_Technomancer.crossroads.integration.JEI.FluidCoolingRecipe;
import com.Da_Technomancer.crossroads.integration.JEI.GrindstoneRecipe;
import com.Da_Technomancer.crossroads.integration.ModIntegration;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * Integration for the Fluid Cooling Chamber
 */
@ZenClass("mods.crossroads.FluidCoolingChamber")
public class FluidCoolingChamberHandler {
    /**
     * Add a new fluid cooling chamber recipe that produces the output stack from the input liquid.
     *
     * @param output the result of the recipe
     * @param input the input liquid and amount that is required to produce the result
     * @param maxTemp the maximum temperature allowed
     * @param heatAdded the amount of heat that is added
     */
    @ZenMethod
    public static void addRecipe(IItemStack output, ILiquidStack input, double maxTemp, double heatAdded) {
        FluidStack liquidStack = MineTweakerMC.getLiquidStack(input);
        if (liquidStack == null) {
            return;
        }
        Fluid fluid = liquidStack.getFluid();
        int amountRequired = liquidStack.amount;
        ItemStack outputStack = MineTweakerMC.getItemStack(output);
        MineTweakerAPI.apply(new Add(fluid, amountRequired, outputStack, maxTemp, heatAdded));
    }

    /**
     * Operation to add a recipe.
     */
    private static class Add implements IUndoableAction {
        boolean success = false;
        final Fluid fluid;
        final Pair<Integer, Triple<ItemStack, Double, Double>> recipe;
        Pair<Integer, Triple<ItemStack, Double, Double>> overwrittenValue;

        private Add(Fluid fluid, Integer amount, ItemStack output, Double maxTemp, Double heatAdded) {
            this.fluid = fluid;
            this.recipe = Pair.of(amount, Triple.of(output, maxTemp, heatAdded));
        }


        @Override
        public void apply() {
            overwrittenValue = RecipeHolder.fluidCoolingRecipes.put(fluid, recipe);
            success = true;
            MineTweakerIntegration.refreshJEI();
        }

        @Override
        public boolean canUndo() {
            return success;
        }

        @Override
        public void undo() {
            if (success) {
                // Remove the recipe if it was registered by this action
                boolean wasRemoved = RecipeHolder.fluidCoolingRecipes.remove(fluid, recipe);
                if (overwrittenValue != null && wasRemoved) {
                    // add the previous recipe again
                    RecipeHolder.fluidCoolingRecipes.put(fluid, overwrittenValue);
                }

                MineTweakerIntegration.refreshJEI();
            }
        }

        @Override
        public String describe() {
            return "Adding fluid cooling recipe for " + fluid.getName();
        }

        @Override
        public String describeUndo() {
            return "Removing fluid cooling recipe for " + fluid.getName();
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    /**
     * Remove the cooling chamber recipe for the input liquid.
     *
     * @param input the input liquid of the recipe
     */
    @ZenMethod
    public static void removeRecipe(ILiquidStack input) {
        FluidStack liquidStack = MineTweakerMC.getLiquidStack(input);
        if (liquidStack != null) {
            MineTweakerAPI.apply(new Remove(liquidStack.getFluid()));
        }
    }

    /**
     * Operation to remove a recipe.
     */
    private static class Remove implements IUndoableAction {
        Fluid fluid;
        Pair<Integer, Triple<ItemStack, Double, Double>> recipe;
        boolean success = false;

        Remove(Fluid fluid) {
            this.fluid = fluid;
        }

        @Override
        public void apply() {
            recipe = RecipeHolder.fluidCoolingRecipes.remove(fluid);
            success = recipe != null;
            MineTweakerIntegration.refreshJEI();
        }

        @Override
        public boolean canUndo() {
            return success;
        }

        @Override
        public void undo() {
            if (success) {
                RecipeHolder.fluidCoolingRecipes.put(fluid, recipe);
                MineTweakerIntegration.refreshJEI();
            }
        }

        @Override
        public String describe() {
            return "Removing fluid cooling recipe for " + fluid.getName();
        }

        @Override
        public String describeUndo() {
            return "Adding fluid cooling recipe for " + fluid.getName();
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}

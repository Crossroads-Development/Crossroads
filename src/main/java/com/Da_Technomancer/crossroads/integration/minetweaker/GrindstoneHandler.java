package com.Da_Technomancer.crossroads.integration.minetweaker;

import com.Da_Technomancer.crossroads.integration.JEI.GrindstoneRecipe;
import com.Da_Technomancer.crossroads.integration.ModIntegration;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * Integration for the Grindstone.
 */
@ZenClass("mods.crossroads.Grindstone")
public class GrindstoneHandler {
    /**
     * Add a new recipe.
     *
     * @param input   input item stack
     * @param output1 first output
     * @param output2 second output, optional
     * @param output3 third output, optional
     */
    @ZenMethod
    public static void addRecipe(IItemStack input, IIngredient output1, @Optional IIngredient output2, @Optional IIngredient output3) {
        String key = getItemStackKey(input);
        if (key == null) {
            return;
        }
        MineTweakerAPI.apply(new Add(key, MineTweakerIntegration.toItemStack(output1, output2, output3)));
    }

    /**
     * Calculate the key for the recipe map from the item stack.
     *
     * @param input the input item stack
     * @return the key for the recipe map
     */
    private static String getItemStackKey(IItemStack input) {
        String key = null;
        ItemStack itemStack = MineTweakerMC.getItemStack(input);
        if (itemStack != null) {
            Item item = itemStack.getItem();
            key = item.getRegistryName().toString();
        }
        return key;
    }

    /**
     * Add a new ore dict recipe.
     *
     * @param input   the ore dictionary entry for the input
     * @param output1 first output
     * @param output2 second output, optional
     * @param output3 third output, optional
     */
    @ZenMethod
    public static void addRecipe(IOreDictEntry input, IIngredient output1, @Optional IIngredient output2, @Optional IIngredient output3) {
        String key = input.getName();
        if (key == null) {
            return;
        }
        MineTweakerAPI.apply(new Add(key, MineTweakerIntegration.toItemStack(output1, output2, output3)));
    }

    /**
     * Adds a new grindstone recipe and updates JEI Integration.
     *
     * @param input   the key that represents the input item
     * @param outputs the results of the grindstone operation
     */
    private static ItemStack[] addGrindstoneRecipe(String input, ItemStack[] outputs) {
        return RecipeHolder.grindRecipes.put(input, outputs);
    }

    /**
     * Remove the recipe for the grindstone and return the previously registered output stack for that key (if it exists).
     * If the outputs are specified via the parameter, the recipe is only removed if the output stacks matches!
     *
     * @param input       key for the input of the grindstone recipe
     * @param outputs     output stacks of the recipe, can be null if it should be removed regardless of what output was registered
     * @param overwritten the previously registered output for the input, that will be added again
     * @return the output stack that was registered for the input key
     */
    private static ItemStack[] replaceGrindstoneRecipe(String input, ItemStack[] outputs, ItemStack[] overwritten) {
        if (outputs != null) {
            // remove only if the exact output is registered
            if (RecipeHolder.grindRecipes.remove(input, outputs)) {
                if (overwritten != null) {
                    addGrindstoneRecipe(input, overwritten);
                }
                return outputs;
            } else {
                return null;
            }
        } else {
            // remove any recipe registered for that entry
            ItemStack[] removedStack = RecipeHolder.grindRecipes.remove(input);
            if (overwritten != null) {
                addGrindstoneRecipe(input, overwritten);
            }
            return removedStack;
        }
    }

    /**
     * Operation to add a new recipe.
     */
    private static class Add implements IUndoableAction {
        final String input;
        final ItemStack[] outputs;
        ItemStack[] overwritten;

        boolean success = false;

        Add(String input, ItemStack[] outputs) {
            this.input = input;
            this.outputs = outputs;
        }

        @Override
        public void apply() {
            overwritten = addGrindstoneRecipe(input, outputs);
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
                replaceGrindstoneRecipe(input, outputs, overwritten);
                MineTweakerIntegration.refreshJEI();
            }
        }

        @Override
        public String describe() {
            return "Adding Grindstone recipe for " + input;
        }

        @Override
        public String describeUndo() {
            return "Removing Grindstone recipe for " + input;
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    /**
     * Remove a grindstone recipe for the input item stack.
     *
     * @param input the input for the recipe
     */
    @ZenMethod
    public static void removeRecipe(IItemStack input) {
        String key = getItemStackKey(input);
        if (key == null)
            return;

        MineTweakerAPI.apply(new Remove(key));
    }

    /**
     * Remove a grindstone recipe for the ore dictionary entry of the input item stack.
     *
     * @param input
     */
    @ZenMethod
    public static void removeRecipe(IOreDictEntry input) {
        String key = input.getName();
        if (key == null)
            return;

        MineTweakerAPI.apply(new Remove(key));
    }

    /**
     * Operation to remove a grindstone recipe.
     */
    private static class Remove implements IUndoableAction {
        String input;
        ItemStack[] outputs;
        boolean success = false;

        Remove(String input) {
            this.input = input;
        }

        @Override
        public void apply() {
            outputs = replaceGrindstoneRecipe(input, null, null);
            success = outputs != null;
            MineTweakerIntegration.refreshJEI();
        }

        @Override
        public boolean canUndo() {
            return success;
        }

        @Override
        public void undo() {
            if (success) {
                addGrindstoneRecipe(input, outputs);
                MineTweakerIntegration.refreshJEI();
            }
        }

        @Override
        public String describe() {
            return "Removing grindstone recipe for " + input;
        }

        @Override
        public String describeUndo() {
            return "Adding grindstone recipe for " + input;
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}

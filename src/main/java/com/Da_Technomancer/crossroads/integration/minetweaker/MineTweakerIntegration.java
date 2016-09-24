package com.Da_Technomancer.crossroads.integration.minetweaker;

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

        ItemStack[] itemStacks = new ItemStack[ingredients.length];
        for (int i = 0; i < ingredients.length; i++) {
            itemStacks[i] = MineTweakerMC.getItemStack(ingredients[i]);
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

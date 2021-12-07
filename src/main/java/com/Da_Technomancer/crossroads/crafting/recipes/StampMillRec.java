package com.Da_Technomancer.crossroads.crafting.recipes;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class StampMillRec extends SingleIngrRecipe{

	public StampMillRec(ResourceLocation location, String name, Ingredient input, ItemStack output, boolean active){
		super(CRRecipes.STAMP_MILL_TYPE, CRRecipes.STAMP_MILL_SERIAL, location, name, input, output, active);
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRBlocks.stampMill);
	}
}

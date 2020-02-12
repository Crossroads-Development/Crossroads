package com.Da_Technomancer.crossroads.items.crafting.recipes;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.crafting.CRRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class StampMillRec extends SingleIngrRecipe{

	public StampMillRec(ResourceLocation location, String name, Ingredient input, ItemStack output){
		super(CRRecipes.STAMP_MILL_TYPE, CRRecipes.STAMP_MILL_SERIAL, location, name, input, output);
	}

	@Override
	public ItemStack getIcon(){
		return new ItemStack(CRBlocks.stampMill);
	}
}

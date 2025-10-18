package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class StampMillRec extends SingleIngrRecipe{

	protected StampMillRec(){
		super();
	}

	protected StampMillRec(String name, Ingredient input, ItemStack output){
		super(name, input, output);
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.STAMP_MILL_TYPE;
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.STAMP_MILL_SERIAL;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRBlocks.stampMill);
	}
}

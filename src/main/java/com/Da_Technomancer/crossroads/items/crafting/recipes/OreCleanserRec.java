package com.Da_Technomancer.crossroads.items.crafting.recipes;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.crafting.CRRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class OreCleanserRec extends SingleIngrRecipe{

	public OreCleanserRec(ResourceLocation location, String name, Ingredient input, ItemStack output){
		super(CRRecipes.ORE_CLEANSER_TYPE, CRRecipes.ORE_CLEANSER_SERIAL, location, name, input, output);
	}

	@Override
	public ItemStack getIcon(){
		return new ItemStack(CRBlocks.oreCleanser);
	}
}

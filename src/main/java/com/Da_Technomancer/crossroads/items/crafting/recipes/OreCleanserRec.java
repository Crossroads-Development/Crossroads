package com.Da_Technomancer.crossroads.items.crafting.recipes;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.crafting.CRRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class OreCleanserRec extends SingleIngrRecipe{

	public OreCleanserRec(ResourceLocation location, String name, Ingredient input, ItemStack output, boolean active){
		super(CRRecipes.ORE_CLEANSER_TYPE, CRRecipes.ORE_CLEANSER_SERIAL, location, name, input, output, active);
	}

	@Override
	public ItemStack getIcon(){
		return new ItemStack(CRBlocks.oreCleanser);
	}
}

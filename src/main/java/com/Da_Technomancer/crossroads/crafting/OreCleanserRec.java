package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class OreCleanserRec extends SingleIngrRecipe{

	public OreCleanserRec(ResourceLocation location, String name, Ingredient input, ItemStack output, boolean active){
		super(CRRecipes.ORE_CLEANSER_TYPE, CRRecipes.ORE_CLEANSER_SERIAL, location, name, input, output, active);
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRBlocks.oreCleanser);
	}
}

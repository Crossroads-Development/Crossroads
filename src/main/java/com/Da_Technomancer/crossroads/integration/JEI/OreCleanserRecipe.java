package com.Da_Technomancer.crossroads.integration.JEI;

import net.minecraft.item.ItemStack;

import java.util.List;

public class OreCleanserRecipe{

	protected final List<ItemStack> inputs;
	protected final ItemStack output;

	public OreCleanserRecipe(List<ItemStack> input, ItemStack output){
		inputs = input;
		this.output = output;
	}
}

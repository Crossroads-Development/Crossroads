package com.Da_Technomancer.crossroads.integration.JEI;

import net.minecraft.item.ItemStack;

import java.util.List;

public class StampMillRecipe{

	protected List<ItemStack> inputs;
	protected ItemStack output;

	public StampMillRecipe(List<ItemStack> input, ItemStack output){
		inputs = input;
		this.output = output;
	}
}

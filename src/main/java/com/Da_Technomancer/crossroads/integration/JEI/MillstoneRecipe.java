package com.Da_Technomancer.crossroads.integration.JEI;

import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public class MillstoneRecipe{

	protected final List<ItemStack> inputs;
	protected final List<ItemStack> outputs;

	public MillstoneRecipe(List<ItemStack> input, ItemStack[] output){
		inputs = input;
		outputs = Arrays.asList(output);
	}
}

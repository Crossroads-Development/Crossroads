package com.Da_Technomancer.crossroads.integration.JEI;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class BlastFurnaceRecipe{

	protected final List<ItemStack> input;
	protected final FluidStack output;
	protected final int slag;

	public BlastFurnaceRecipe(List<ItemStack> input, FluidStack output, int slag){
		this.input = input;
		this.output = output;
		this.slag = slag;
	}
}

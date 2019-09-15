package com.Da_Technomancer.crossroads.integration.JEI;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Map.Entry;

public class FluidCoolingRecipe{

	protected final FluidStack fluid;
	protected final ItemStack stack;
	protected final double max;
	protected final double add;

	public FluidCoolingRecipe(Entry<Fluid, Pair<Integer, Triple<ItemStack, Double, Double>>> entry){
		fluid = new FluidStack(entry.getKey(), entry.getValue().getLeft());
		stack = entry.getValue().getRight().getLeft();
		max = entry.getValue().getRight().getMiddle();
		add = entry.getValue().getRight().getRight();
	}
}

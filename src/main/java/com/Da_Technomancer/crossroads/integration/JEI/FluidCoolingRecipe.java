package com.Da_Technomancer.crossroads.integration.JEI;

import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FluidCoolingRecipe{
	
	private final FluidStack fluid;
	private final ItemStack stack;
	private final double max;
	private final double add;
	
	public FluidCoolingRecipe(Entry<Fluid, Pair<Integer, Triple<ItemStack, Double, Double>>> entry){
		fluid = new FluidStack(entry.getKey(), entry.getValue().getLeft());
		stack = entry.getValue().getRight().getLeft();
		max = entry.getValue().getRight().getMiddle();
		add = entry.getValue().getRight().getRight();
	}
	
	protected FluidStack getFluid(){
		return fluid;
	}
	
	protected ItemStack getStack(){
		return stack;
	}
	
	protected double getMax(){
		return max;
	}
	
	protected double getAdd(){
		return add;
	}

}

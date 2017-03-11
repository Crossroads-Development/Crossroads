package com.Da_Technomancer.crossroads.integration.JEI;

import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class HeatExchangerRecipe{

	private final ItemStack input;
	private final ItemStack stack;
	private final FluidStack inputFluid;
	private final FluidStack outputFluid;
	private final double max;
	private final double add;

	public HeatExchangerRecipe(Entry<Block, Triple<IBlockState, Double, Double>> entry){
		if(entry.getKey() == Blocks.FIRE){
			ItemStack discountFire = new ItemStack(Items.FLINT_AND_STEEL, 1);
			discountFire.setStackDisplayName("Fire");
			input = discountFire;
		}else{
			input = new ItemStack(entry.getKey(), 1);
		}
		if(input.isEmpty() && entry.getKey() != null && FluidRegistry.lookupFluidForBlock(entry.getKey()) != null){
			inputFluid = new FluidStack(FluidRegistry.lookupFluidForBlock(entry.getKey()), 1);
		}else{
			inputFluid = null;
		}
		stack = entry.getValue().getLeft() == null ? ItemStack.EMPTY : new ItemStack(entry.getValue().getLeft().getBlock());
		if(stack.isEmpty() && entry.getValue().getLeft() != null && FluidRegistry.lookupFluidForBlock(entry.getValue().getLeft().getBlock()) != null){
			outputFluid = new FluidStack(FluidRegistry.lookupFluidForBlock(entry.getValue().getLeft().getBlock()), 1000);
		}else{
			outputFluid = null;
		}
		add = entry.getValue().getMiddle();
		max = entry.getValue().getRight();
	}

	protected ItemStack getInput(){
		return input;
	}

	protected ItemStack getStack(){
		return stack;
	}
	
	protected FluidStack getInputFluid(){
		return inputFluid;
	}

	protected FluidStack getOutputFluid(){
		return outputFluid;
	}

	protected double getMax(){
		return max;
	}

	protected double getAdd(){
		return add;
	}

}

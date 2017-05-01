package com.Da_Technomancer.crossroads.integration.JEI;

import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Triple;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class HeatExchangerRecipe implements IRecipeWrapper{

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

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY){
		minecraft.fontRenderer.drawString((add < 0 ? "Minimum temp: " : "Maximum temp: ") + max + "°C", 10, 10, 4210752);
		minecraft.fontRenderer.drawString("Heat Added: " + add + "°C", 10, 20, 4210752);
		if(inputFluid != null){
			minecraft.fontRenderer.drawString("Does not require source block", 10, 30, 4210752);
		}
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY){
		return null;
	}

	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton){
		return false;
	}

	@Override
	public void getIngredients(IIngredients ingredients){
		ingredients.setOutput(ItemStack.class, stack);
		ingredients.setInput(ItemStack.class, input);
		ingredients.setOutput(FluidStack.class, outputFluid);
		ingredients.setInput(FluidStack.class, inputFluid);
	}
}

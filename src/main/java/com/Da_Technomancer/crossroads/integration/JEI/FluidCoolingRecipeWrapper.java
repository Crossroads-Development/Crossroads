package com.Da_Technomancer.crossroads.integration.JEI;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;

import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class FluidCoolingRecipeWrapper implements IRecipeWrapper{
	
	private final List<ItemStack> outputs;
	private final List<FluidStack> input;
	private final double max;
	private final double add;
	
	protected FluidCoolingRecipeWrapper(@Nonnull FluidCoolingRecipe recipe){
		outputs = ImmutableList.of(recipe.getStack());
		input = ImmutableList.of(recipe.getFluid());
		max = recipe.getMax();
		add = recipe.getAdd();
	}
	
	@Override
	public List<ItemStack> getInputs(){
		return ImmutableList.of();
	}

	@Override
	public List<ItemStack> getOutputs(){
		return outputs;
	}

	@Override
	public List<FluidStack> getFluidInputs(){
		return input;
	}

	@Override
	public List<FluidStack> getFluidOutputs(){
		return ImmutableList.of();
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY){
		minecraft.fontRendererObj.drawString("Maximum temp: " + max + "*C", 10, 10, 4210752);
		minecraft.fontRendererObj.drawString("Heat Added: " + add + "*C", 10, 20, 4210752);
	}

	@Override
	public void drawAnimations(Minecraft minecraft, int recipeWidth, int recipeHeight){
		
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY){
		return null;
	}

	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton){
		return false;
	}

}

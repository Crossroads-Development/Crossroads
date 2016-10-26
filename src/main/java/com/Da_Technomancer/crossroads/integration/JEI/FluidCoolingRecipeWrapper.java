package com.Da_Technomancer.crossroads.integration.JEI;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class FluidCoolingRecipeWrapper implements IRecipeWrapper{

	private final ItemStack output;
	private final FluidStack input;
	private final double max;
	private final double add;

	protected FluidCoolingRecipeWrapper(@Nonnull FluidCoolingRecipe recipe){
		output = recipe.getStack();
		input = recipe.getFluid();
		max = recipe.getMax();
		add = recipe.getAdd();
	}

	@Override
	@Deprecated
	public List<ItemStack> getInputs(){
		return ImmutableList.of();
	}

	@Override
	@Deprecated
	public List<ItemStack> getOutputs(){
		return ImmutableList.of(output);
	}

	@Override
	@Deprecated
	public List<FluidStack> getFluidInputs(){
		return ImmutableList.of(input);
	}

	@Override
	@Deprecated
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

	@Override
	public void getIngredients(IIngredients ingredients){
		ingredients.setOutput(ItemStack.class, output);
		ingredients.setInput(FluidStack.class, input);
	}

}

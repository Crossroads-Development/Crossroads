package com.Da_Technomancer.crossroads.integration.JEI;

import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class HeatExchangerRecipeWrapper implements IRecipeWrapper{

	private final ItemStack output;
	private final ItemStack input;
	private final FluidStack inputFluid;
	private final FluidStack outputFluid;
	private final double max;
	private final double add;

	protected HeatExchangerRecipeWrapper(@Nonnull HeatExchangerRecipe recipe){
		output = recipe.getStack();
		input = recipe.getInput();
		max = recipe.getMax();
		add = recipe.getAdd();
		inputFluid = recipe.getInputFluid();
		outputFluid = recipe.getOutputFluid();
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY){
		minecraft.fontRendererObj.drawString((add < 0 ? "Minimum temp: " : "Maximum temp: ") + max + "°C", 10, 10, 4210752);
		minecraft.fontRendererObj.drawString("Heat Added: " + add + "°C", 10, 20, 4210752);
		if(inputFluid != null){
			minecraft.fontRendererObj.drawString("Does not require source block", 10, 30, 4210752);
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
		ingredients.setOutput(ItemStack.class, output);
		ingredients.setInput(ItemStack.class, input);
		ingredients.setOutput(FluidStack.class, outputFluid);
		ingredients.setInput(FluidStack.class, inputFluid);
	}
}

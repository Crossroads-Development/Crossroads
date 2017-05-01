package com.Da_Technomancer.crossroads.integration.JEI;

import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FluidCoolingRecipe implements IRecipeWrapper{

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

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY){
		minecraft.fontRenderer.drawString("Maximum temp: " + max + "°C", 10, 10, 4210752);
		minecraft.fontRenderer.drawString("Heat Added: " + add + "°C", 10, 20, 4210752);
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
		ingredients.setInput(FluidStack.class, fluid);
	}
}

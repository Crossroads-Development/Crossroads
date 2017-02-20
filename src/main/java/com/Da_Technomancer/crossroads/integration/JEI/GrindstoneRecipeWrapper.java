package com.Da_Technomancer.crossroads.integration.JEI;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class GrindstoneRecipeWrapper implements IRecipeWrapper{

	private List<ItemStack> inputs;
	private List<ItemStack> outputs;

	protected GrindstoneRecipeWrapper(@Nonnull GrindstoneRecipe recipe){
		outputs = Arrays.asList(recipe.getStacks());
		inputs = recipe.getInput().getMatchingList();
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY){

	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY){
		return ImmutableList.of();
	}

	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton){
		return false;
	}

	@Override
	public void getIngredients(IIngredients ingredients){
		ingredients.setInputLists(ItemStack.class, ImmutableList.of(inputs));
		ingredients.setOutputs(ItemStack.class, outputs);
	}

}

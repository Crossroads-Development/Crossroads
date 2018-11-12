package com.Da_Technomancer.crossroads.integration.JEI;

import com.google.common.collect.ImmutableList;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.List;

public class OreCleanserRecipe implements IRecipeWrapper{

	private List<ItemStack> inputs;
	private ItemStack output;

	public OreCleanserRecipe(List<ItemStack> input, ItemStack output){
		inputs = input;
		this.output = output;
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
		ingredients.setOutput(ItemStack.class, output);
	}
}

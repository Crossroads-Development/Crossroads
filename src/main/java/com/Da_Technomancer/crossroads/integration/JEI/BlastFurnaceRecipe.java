package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.items.ModItems;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class BlastFurnaceRecipe implements IRecipeWrapper{

	private final List<ItemStack> input;
	private final FluidStack output;
	private final int slag;

	public BlastFurnaceRecipe(List<ItemStack> input, FluidStack output, int slag){
		this.input = input;
		this.output = output;
		this.slag = slag;
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY){
		minecraft.fontRenderer.drawString("Consumed Carbon: " + slag, 10, 10, 0x404040);
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
		ingredients.setInput(ItemStack.class, input);
		ingredients.setOutput(FluidStack.class, output);
		ingredients.setOutput(ItemStack.class, new ItemStack(ModItems.slag, slag));
	}
}

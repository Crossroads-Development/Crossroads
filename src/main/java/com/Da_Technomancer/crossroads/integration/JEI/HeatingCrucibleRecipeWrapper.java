package com.Da_Technomancer.crossroads.integration.JEI;

import java.util.List;

import javax.annotation.Nonnull;

import com.Da_Technomancer.crossroads.fluids.BlockMoltenCopper;
import com.google.common.collect.ImmutableList;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class HeatingCrucibleRecipeWrapper implements IRecipeWrapper{

	private final boolean copper;

	protected HeatingCrucibleRecipeWrapper(@Nonnull HeatingCrucibleRecipe recipe){
		copper = recipe.isCopper();
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY){
		minecraft.fontRendererObj.drawString("When above 1000*C", 10, 10, 4210752);
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
		ingredients.setInputLists(ItemStack.class, ImmutableList.of(OreDictionary.getOres(copper ? "dustCopper" : "cobblestone", false)));
		ingredients.setOutput(FluidStack.class, new FluidStack(copper ? BlockMoltenCopper.getMoltenCopper() : FluidRegistry.LAVA, 200));
	}

}

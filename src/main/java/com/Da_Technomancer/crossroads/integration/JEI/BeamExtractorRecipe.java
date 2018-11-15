package com.Da_Technomancer.crossroads.integration.JEI;

import java.util.List;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class BeamExtractorRecipe implements IRecipeWrapper{

	private final ItemStack in;
	private final BeamUnit out;

	public BeamExtractorRecipe(ItemStack in, BeamUnit out){
		this.in = in;
		this.out = out;
	}

	protected BeamUnit getMag(){
		return out;
	}
	
	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY){
		minecraft.fontRenderer.drawString("Energy: " + out.getEnergy(), 80, 25, 4210752);
		minecraft.fontRenderer.drawString("Potential: " + out.getPotential(), 80, 45, 4210752);
		minecraft.fontRenderer.drawString("Stability: " + out.getStability(), 80, 65, 4210752);
		minecraft.fontRenderer.drawString("Void: " + out.getVoid(), 80, 85, 4210752);
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
		ingredients.setInput(ItemStack.class, in);
	}
}

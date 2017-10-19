package com.Da_Technomancer.crossroads.integration.JEI;

import java.util.List;

import com.Da_Technomancer.crossroads.API.magic.MagicUnit;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class ArcaneExtractorRecipe implements IRecipeWrapper{

	private final ItemStack in;
	private final MagicUnit out;

	public ArcaneExtractorRecipe(ItemStack in, MagicUnit out){
		this.in = in;
		this.out = out;
	}

	protected MagicUnit getMag(){
		return out;
	}
	
	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY){
		minecraft.fontRenderer.drawString("E:  P:  S:  V:", 80, 50, 4210752);
		minecraft.fontRenderer.drawString(out.getEnergy() + ",", 80, 60, 4210752);
		minecraft.fontRenderer.drawString(out.getPotential() + ",", 96, 60, 4210752);
		minecraft.fontRenderer.drawString(out.getStability() + ",", 112, 60, 4210752);
		minecraft.fontRenderer.drawString(out.getVoid() + "", 128, 60, 4210752);
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

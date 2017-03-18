package com.Da_Technomancer.crossroads.integration.JEI;

import java.util.List;

import javax.annotation.Nonnull;

import com.Da_Technomancer.crossroads.Main;
import com.google.common.collect.ImmutableList;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class DetailedCrafterCategory implements IRecipeCategory<DetailedCrafterRecipeWrapper>{

	protected static final String ID = Main.MODID + ".detailed_crafter";
	private final IDrawable back;
	private final IDrawable gear;

	protected DetailedCrafterCategory(IGuiHelper guiHelper){
		ResourceLocation location = new ResourceLocation(Main.MODID, "textures/gui/container/detailed_crafter.png");
		back = guiHelper.createDrawable(location, 29, 16, 116, 54);
		gear = guiHelper.createDrawable(location, 176, 0, 16, 16);
	}

	@Override
	public String getUid(){
		return ID;
	}

	@Override
	public String getTitle(){
		return "Detailed Crafter";
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft){
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		if(type == 0){
			gear.draw(minecraft, 95, 44);
		}
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
	}
	
	private int type;
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, DetailedCrafterRecipeWrapper recipeWrapper, IIngredients ingredients){
		if(recipeWrapper == null){
			return;
		}
		type = recipeWrapper.getType();
		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 3; y++){
				recipeLayout.getItemStacks().init((y * 3) + x, true, y * 18, x * 18);
				recipeLayout.getItemStacks().set((y * 3) + x, ingredients.getInputs(ItemStack.class).get((y * 3) + x));
			}
		}
		
		recipeLayout.getItemStacks().init(9, false, 94, 18);
		recipeLayout.getItemStacks().set(9, ingredients.getOutputs(ItemStack.class).get(0));
	}

	@Override
	public IDrawable getIcon(){
		return null;
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY){
		return ImmutableList.of();
	}

}

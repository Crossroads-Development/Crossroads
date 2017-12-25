package com.Da_Technomancer.crossroads.integration.JEI;

import java.util.List;

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

public class FusionBeamCategory implements IRecipeCategory<FusionBeamRecipe>{

	public static final String ID = Main.MODID + ".fusion_beam";
	private final IDrawable back;
	private final IDrawable slot;
	private final IDrawable arrowStatic;
	

	protected FusionBeamCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 80);
		slot = guiHelper.getSlotDrawable();

		arrowStatic = guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 79, 35, 24, 17);
	}

	@Override
	public String getUid(){
		return ID;
	}

	@Override
	public String getTitle(){
		return "Fusion Beam";
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void drawExtras(Minecraft minecraft){
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		slot.draw(minecraft, 40, 40);//Input
		slot.draw(minecraft, 120, 40);//Output
		arrowStatic.draw(minecraft, 78, 40);
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, FusionBeamRecipe recipeWrapper, IIngredients ingredients){
		recipeLayout.getItemStacks().init(0, true, 40, 40);
		recipeLayout.getItemStacks().set(0, ingredients.getInputs(ItemStack.class).get(0));
		recipeLayout.getItemStacks().init(1, false, 120, 40);
		recipeLayout.getItemStacks().set(1, ingredients.getOutputs(ItemStack.class).get(0));
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY){
		return ImmutableList.of();
	}
	
	@Override
	public String getModName(){
		return Main.MODNAME;
	}
}

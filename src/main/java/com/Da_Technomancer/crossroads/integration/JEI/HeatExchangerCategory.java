package com.Da_Technomancer.crossroads.integration.JEI;

import java.util.List;

import com.Da_Technomancer.crossroads.Main;
import com.google.common.collect.ImmutableList;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class HeatExchangerCategory implements IRecipeCategory<HeatExchangerRecipe>{

	public static final String ID = Main.MODID + ".heat_exchanger";
	private final IDrawable back;
	private final IDrawable slotOut;
	private final IDrawable slotIn;
	private final IDrawableAnimated arrow;
	private final IDrawableStatic arrowStatic;

	protected HeatExchangerCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		slotOut = guiHelper.getSlotDrawable();
		slotIn = guiHelper.getSlotDrawable();
		arrowStatic = guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 79, 35, 24, 17);
		arrow = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 176, 14, 24, 17), 40, StartDirection.LEFT, false);
	}

	@Override
	public String getUid(){
		return ID;
	}

	@Override
	public String getTitle(){
		return "Heat Exchanger";
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void drawExtras(Minecraft minecraft){
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		slotOut.draw(minecraft, 80, 55);
		slotIn.draw(minecraft, 21, 55);
		arrowStatic.draw(minecraft, 45, 56);
		arrow.draw(minecraft, 45, 56);
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, HeatExchangerRecipe recipeWrapper, IIngredients ingredients){
		if(ingredients.getInputs(FluidStack.class).get(0) != null){
			recipeLayout.getFluidStacks().init(0, true, 22, 56, 17, 17, 1, false, null);
			recipeLayout.getFluidStacks().set(0, ingredients.getInputs(FluidStack.class).get(0));
		}
		recipeLayout.getItemStacks().init(0, true, 21, 55);
		recipeLayout.getItemStacks().set(0, ingredients.getInputs(ItemStack.class).get(0));
		if(ingredients.getOutputs(FluidStack.class).get(0) != null){
			recipeLayout.getFluidStacks().init(1, false, 81, 56, 16, 16, 1000, true, null);
			recipeLayout.getFluidStacks().set(1, ingredients.getOutputs(FluidStack.class).get(0));
		}
		recipeLayout.getItemStacks().init(1, false, 80, 55);
		recipeLayout.getItemStacks().set(1, ingredients.getOutputs(ItemStack.class).get(0));
	}

	@Override
	public IDrawable getIcon(){
		return null;
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

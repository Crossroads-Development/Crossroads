package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.Main;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("rawtypes")
public class FluidCoolingCategory implements IRecipeCategory{

	protected static final String id = Main.MODID + ".fluidCooling";
	private final IDrawable back;
	private final IDrawable slot;
	private final IDrawableAnimated arrow;
	private final IDrawableStatic arrowStatic;

	protected FluidCoolingCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		slot = guiHelper.getSlotDrawable();
		arrowStatic = guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 79, 35, 24, 17);
		arrow = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 176, 14, 24, 17), 40, StartDirection.LEFT, false);
	}

	@Override
	public String getUid(){
		return id;
	}

	@Override
	public String getTitle(){
		return "Fluid Cooling";
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void drawExtras(Minecraft minecraft){
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		slot.draw(minecraft, 80, 55);
		arrowStatic.draw(minecraft, 45, 56);
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
	}

	@Override
	public void drawAnimations(Minecraft minecraft){
		arrow.draw(minecraft, 45, 56);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper){
		if(!(recipeWrapper instanceof FluidCoolingRecipeWrapper)){
			return;
		}
		FluidCoolingRecipeWrapper wrapper = ((FluidCoolingRecipeWrapper) recipeWrapper);
		recipeLayout.getFluidStacks().init(0, true, 21, 30, 16, 64, 1000, true, null);
		recipeLayout.getFluidStacks().set(0, wrapper.getFluidInputs());
		recipeLayout.getItemStacks().init(0, false, 80, 55);
		recipeLayout.getItemStacks().set(0, wrapper.getOutputs());
	}

}

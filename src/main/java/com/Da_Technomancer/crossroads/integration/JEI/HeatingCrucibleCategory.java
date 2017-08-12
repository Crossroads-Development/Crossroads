package com.Da_Technomancer.crossroads.integration.JEI;

import java.util.List;

import com.Da_Technomancer.crossroads.Main;
import com.google.common.collect.ImmutableList;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class HeatingCrucibleCategory implements IRecipeCategory<HeatingCrucibleRecipe>{

	public static final String ID = Main.MODID + ".heating_crucible";
	private final IDrawable back;
	private final IDrawable slot;
	private final IDrawableAnimated arrow;
	private final IDrawable arrowStatic;
	private final IDrawable fluidOverlay;

	protected HeatingCrucibleCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		slot = guiHelper.getSlotDrawable();

		arrowStatic = guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 79, 35, 24, 17);
		arrow = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 176, 14, 24, 17), 40, StartDirection.LEFT, false);
	
		fluidOverlay = guiHelper.createDrawable(new ResourceLocation(Main.MODID, "textures/gui/square_fluid_overlay.png"), 0, 0, 32, 32, 32, 32);
	}

	@Override
	public String getUid(){
		return ID;
	}

	@Override
	public String getTitle(){
		return "Heating Crucible";
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void drawExtras(Minecraft minecraft){
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		slot.draw(minecraft, 40, 50);
		arrowStatic.draw(minecraft, 62, 50);
		arrow.draw(minecraft, 62, 50);
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, HeatingCrucibleRecipe recipeWrapper, IIngredients ingredients){
		List<FluidStack> fluids = ingredients.getOutputs(FluidStack.class).get(0);
		recipeLayout.getFluidStacks().init(0, false, 90, 42, 32, 32, fluids.get(0).amount, false, fluidOverlay);
		recipeLayout.getFluidStacks().set(0, fluids);
		recipeLayout.getItemStacks().init(0, true, 40, 50);
		recipeLayout.getItemStacks().set(0, ingredients.getInputs(ItemStack.class).get(0));
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

package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.Main;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

@SuppressWarnings("rawtypes")
public class HeatingCrucibleCategory implements IRecipeCategory{

	protected static final String id = Main.MODID + ".heatingCrucible";
	private final IDrawable back;
	private final IDrawable slot;
	private final IDrawableAnimated arrow;
	private final IDrawable arrowStatic;

	protected HeatingCrucibleCategory(IGuiHelper guiHelper){
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
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
	}

	@Override
	public void drawAnimations(Minecraft minecraft){
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		arrow.draw(minecraft, 62, 50);
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
	}

	@Override
	@Deprecated
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper){
		if(!(recipeWrapper instanceof HeatingCrucibleRecipeWrapper)){
			return;
		}
		HeatingCrucibleRecipeWrapper wrapper = ((HeatingCrucibleRecipeWrapper) recipeWrapper);
		recipeLayout.getFluidStacks().init(0, false, 90, 42, 32, 32, 200, false, null);
		recipeLayout.getFluidStacks().set(0, wrapper.getFluidOutputs());
		recipeLayout.getItemStacks().init(0, true, 40, 50);
		recipeLayout.getItemStacks().set(0, wrapper.getInputs());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients){
		if(!(recipeWrapper instanceof HeatingCrucibleRecipeWrapper)){
			return;
		}
		recipeLayout.getFluidStacks().init(0, false, 90, 42, 32, 32, 200, false, null);
		recipeLayout.getFluidStacks().set(0, ingredients.getOutputs(FluidStack.class));
		recipeLayout.getItemStacks().init(0, true, 40, 50);
		recipeLayout.getItemStacks().set(0, ingredients.getInputs(ItemStack.class).get(0));
	}
}

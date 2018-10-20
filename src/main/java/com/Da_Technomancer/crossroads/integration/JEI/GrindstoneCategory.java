package com.Da_Technomancer.crossroads.integration.JEI;

import javax.annotation.Nonnull;

import com.Da_Technomancer.crossroads.Main;

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

public class GrindstoneCategory implements IRecipeCategory<GrindstoneRecipe>{

	public static final String ID = Main.MODID + ".grindstone";
	private final IDrawable back;
	private final IDrawable slot;
	private final IDrawableAnimated arrow;
	private final IDrawableStatic arrowStatic;

	protected GrindstoneCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		slot = guiHelper.getSlotDrawable();
		arrowStatic = guiHelper.createDrawable(new ResourceLocation(Main.MODID, "textures/gui/container/millstone_gui.png"), 66, 35, 44, 17);
		arrow = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(new ResourceLocation(Main.MODID, "textures/gui/container/millstone_gui.png"), 176, 0, 44, 17), 40, StartDirection.TOP, false);
	}

	@Override
	public String getUid(){
		return ID;
	}

	@Override
	public String getTitle(){
		return "Millstone";
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft){
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		slot.draw(minecraft, 79, 16);
		slot.draw(minecraft, 61, 52);
		slot.draw(minecraft, 79, 52);
		slot.draw(minecraft, 97, 52);
		arrowStatic.draw(minecraft, 66, 35);
		arrow.draw(minecraft, 66, 35);
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, GrindstoneRecipe recipeWrapper, IIngredients ingredients){
		if(ingredients.getInputs(ItemStack.class).get(0).isEmpty()){
			// Might happen if CraftTweaker added an invalid recipe
			return;
		}

		recipeLayout.getItemStacks().init(0, true, 79, 16);
		recipeLayout.getItemStacks().set(0, ingredients.getInputs(ItemStack.class).get(0));

		recipeLayout.getItemStacks().init(1, false, 61, 52);
		recipeLayout.getItemStacks().init(2, false, 79, 52);
		recipeLayout.getItemStacks().init(3, false, 97, 52);
		recipeLayout.getItemStacks().set(1, ingredients.getOutputs(ItemStack.class).size() >= 1 ? ingredients.getOutputs(ItemStack.class).get(0) : null);
		recipeLayout.getItemStacks().set(2, ingredients.getOutputs(ItemStack.class).size() >= 2 ? ingredients.getOutputs(ItemStack.class).get(1) : null);
		recipeLayout.getItemStacks().set(3, ingredients.getOutputs(ItemStack.class).size() == 3 ? ingredients.getOutputs(ItemStack.class).get(2) : null);
	}

	@Override
	public String getModName(){
		return Main.MODNAME;
	}
}

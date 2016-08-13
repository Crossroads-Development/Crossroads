package com.Da_Technomancer.crossroads.client.integration.JEI;

import javax.annotation.Nonnull;

import com.Da_Technomancer.crossroads.Main;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("rawtypes")
public class GrindstoneCategory implements IRecipeCategory{

	protected static final String id = Main.MODID + ".grindstone";
	private final IDrawable back;
	private final IDrawable overlay;
	
	protected GrindstoneCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 76);
		overlay = guiHelper.createDrawable(new ResourceLocation(Main.MODID + ":textures/gui/container/grindstoneGui.png"), 61, 16, 54, 54);
	}
	
	@Override
	public String getUid(){
		return id;
	}

	@Override
	public String getTitle(){
		return "Grindstone";
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft){
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		overlay.draw(minecraft, 61, 16);
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
	}

	@Override
	public void drawAnimations(Minecraft minecraft){
		
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper){
		if(!(recipeWrapper instanceof GrindstoneRecipeWrapper))
			return;
		GrindstoneRecipeWrapper wrapper = ((GrindstoneRecipeWrapper) recipeWrapper);

		recipeLayout.getItemStacks().init(0, true, 79, 16);
		recipeLayout.getItemStacks().set(0, wrapper.getInputs().get(0));
		
		recipeLayout.getItemStacks().init(1, false, 61, 52);
		recipeLayout.getItemStacks().init(2, false, 79, 52);
		recipeLayout.getItemStacks().init(3, false, 97, 52);
		recipeLayout.getItemStacks().set(1, wrapper.getOutputs().size() >= 1 ? wrapper.getOutputs().get(0) : null);
		recipeLayout.getItemStacks().set(2, wrapper.getOutputs().size() >= 2 ? wrapper.getOutputs().get(1) : null);
		recipeLayout.getItemStacks().set(3, wrapper.getOutputs().size() == 3 ? wrapper.getOutputs().get(2) : null);
	}

}

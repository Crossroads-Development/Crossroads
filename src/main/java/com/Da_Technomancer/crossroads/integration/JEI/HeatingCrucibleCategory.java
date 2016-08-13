package com.Da_Technomancer.crossroads.integration.JEI;

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
public class HeatingCrucibleCategory implements IRecipeCategory{

	protected static final String id = Main.MODID + ".heatingCrucible";
	private final IDrawable back;
	private final IDrawable arrowSlot;


	protected HeatingCrucibleCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		arrowSlot = guiHelper.createDrawable(new ResourceLocation("textures/gui/container/crafting_table.png"), 65, 34, 47, 18);
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
		arrowSlot.draw(minecraft, 40, 50);
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
	}

	@Override
	public void drawAnimations(Minecraft minecraft){

	}

	@Override
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
}

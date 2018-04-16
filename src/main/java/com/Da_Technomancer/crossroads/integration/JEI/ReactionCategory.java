package com.Da_Technomancer.crossroads.integration.JEI;

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
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;

public class ReactionCategory implements IRecipeCategory<ReactionRecipe>{

	public static final String ID = Main.MODID + ".reaction";
	protected static final ResourceLocation ICONS = new ResourceLocation(Main.MODID, "textures/gui/icons.png");

	private final IDrawable back;
	private final IDrawableAnimated arrow;
	private final IDrawableStatic arrowStatic;

	protected ReactionCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		arrowStatic = guiHelper.createDrawable(ICONS, 32, 0, 24, 16);
		arrow = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(ICONS, 32, 16, 24, 16), 40, StartDirection.LEFT, false);
	}

	@Override
	public String getUid(){
		return ID;
	}

	@Override
	public String getTitle(){
		return "Alchemical Reaction";
	}

	@Override
	public String getModName(){
		return Main.MODNAME;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft){
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		arrowStatic.draw(minecraft, 78, 22);
		arrow.draw(minecraft, 78, 22);
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, ReactionRecipe recipe, IIngredients ingredients){
		int inCount = ingredients.getInputs(ReagIngr.class).size();
		for(int i = 0; i < inCount; i++){
			List<ReagIngr> r = ingredients.getInputs(ReagIngr.class).get(i);
			recipeLayout.getIngredientsGroup(ReagIngr.class).init(i, true, 60 - i * 20, 20);
			recipeLayout.getIngredientsGroup(ReagIngr.class).set(i, r);
		}
		int outCount = ingredients.getOutputs(ReagIngr.class).size();
		for(int i = 0; i < outCount; i++ ){
			List<ReagIngr> r = ingredients.getOutputs(ReagIngr.class).get(i);
			recipeLayout.getIngredientsGroup(ReagIngr.class).init(i + inCount, false, 105 + i * 20, 20);
			recipeLayout.getIngredientsGroup(ReagIngr.class).set(i + inCount, r);
		}
	}
}

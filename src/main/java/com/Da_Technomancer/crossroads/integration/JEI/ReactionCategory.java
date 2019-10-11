package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.google.common.collect.ImmutableList;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;

public class ReactionCategory implements IRecipeCategory<ReactionRecipe>{

	public static final ResourceLocation ID = new ResourceLocation(Crossroads.MODID, "reaction");
	protected static final ResourceLocation ICONS = new ResourceLocation(Crossroads.MODID, "textures/gui/icons.png");

	private final IDrawable back;
	private final IDrawableAnimated arrow;
	private final IDrawableStatic arrowStatic;
	private final IDrawable icon;
	private final IDrawable bolt;
	private final IDrawable blast;

	protected ReactionCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		arrowStatic = guiHelper.createDrawable(ICONS, 32, 0, 24, 16);
		arrow = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(ICONS, 32, 16, 24, 16), 40, IDrawableAnimated.StartDirection.LEFT, false);
		icon = guiHelper.createDrawableIngredient(new ItemStack(CRItems.florenceFlaskCrystal, 1));
		bolt = guiHelper.createDrawable(ReactionCategory.ICONS, 16, 0, 16, 16);
		blast = guiHelper.createDrawable(ReactionCategory.ICONS, 64, 0, 16, 16);
	}

	@Override
	public ResourceLocation getUid(){
		return ID;
	}

	@Override
	public Class<? extends ReactionRecipe> getRecipeClass(){
		return ReactionRecipe.class;
	}

	@Override
	public String getTitle(){
		return "Alchemical Reaction";//TODO localize
	}

	@Override
	public IDrawable getIcon(){
		return icon;
	}

	@Override
	public void setIngredients(ReactionRecipe recipe, IIngredients ingredients){
		ingredients.setInputs(ReagIngr.REAG, recipe.ingr);
		ingredients.setOutputs(ReagIngr.REAG, recipe.prod);
	}

	@Override
	public void draw(ReactionRecipe recipe, double mouseX, double mouseY){
//		GlStateManager.enableAlpha();
//		GlStateManager.enableBlend();
		arrowStatic.draw(78, 22);
		arrow.draw(78, 22);
//		GlStateManager.disableBlend();
//		GlStateManager.disableAlpha();

		FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
		String line = (recipe.minTemp <= -273 ? "-273" : recipe.minTemp) + "" + (recipe.maxTemp >= Short.MAX_VALUE - 100 ? "°C and up" : " to " + recipe.maxTemp + "°C");
		fontRenderer.drawString(line, 90 - fontRenderer.getStringWidth(line) / 2, 42, 4210752);
		line = recipe.deltaHeat > 0 ? "Endothermic" : recipe.deltaHeat < 0 ? "Exothermic" : "Isothermic";//TODO localize
		fontRenderer.drawString(line, 90 - fontRenderer.getStringWidth(line) / 2, 62, 4210752);

		if(recipe.charged){
//			GlStateManager.color(1, 1, 1);
			bolt.draw(66, 2);
		}

		if(recipe.dangerous){
//			GlStateManager.color(1, 1, 1);
			blast.draw(98, 2);
		}

		if(recipe.catalyst != null){
//			GlStateManager.color(1, 1, 1);
			ReagentIngredientRenderer.RENDERER.render(82, 2, new ReagIngr(recipe.catalyst, 0));
		}
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void setRecipe(IRecipeLayout layout, ReactionRecipe recipe, IIngredients ingredients){
		IGuiIngredientGroup<ReagIngr> reagGroup = layout.getIngredientsGroup(ReagIngr.REAG);
		int inCount = recipe.ingr.size();

		for(int i = 0; i < inCount; i++){
			reagGroup.init(i, true, 60 - i * 20, 20);
			reagGroup.set(i, recipe.ingr.get(i));
		}
		int outCount = recipe.prod.size();
		for(int i = 0; i < outCount; i++ ){
			reagGroup.init(i + inCount, false, 105 + i * 20, 20);
			reagGroup.set(i + inCount, recipe.prod.get(i));
		}

		reagGroup.set(ingredients);
	}

	@Override
	public List<String> getTooltipStrings(ReactionRecipe recipe, double mouseX, double mouseY){
		if(recipe.catalyst != null && mouseX >= 82 && mouseX <= 98 && mouseY >= 2 && mouseY <= 18){
			return ImmutableList.of(recipe.catalyst.getName());
		}
		return Collections.emptyList();
	}
}

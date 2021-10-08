package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.recipes.MillRec;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

public class MillstoneCategory implements IRecipeCategory<MillRec>{

	public static final ResourceLocation ID = new ResourceLocation(Crossroads.MODID, ".millstone");
	private final IDrawable back;
	private final IDrawable slot;
	private final IDrawable icon;
	private final IDrawableAnimated arrow;
	private final IDrawableStatic arrowStatic;

	protected MillstoneCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		slot = guiHelper.getSlotDrawable();
		arrowStatic = guiHelper.createDrawable(new ResourceLocation(Crossroads.MODID, "textures/gui/container/millstone_gui.png"), 66, 35, 44, 17);
		arrow = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(new ResourceLocation(Crossroads.MODID, "textures/gui/container/millstone_gui.png"), 176, 0, 44, 17), 40, IDrawableAnimated.StartDirection.TOP, false);
		icon = guiHelper.createDrawableIngredient(new ItemStack(CRBlocks.millstone, 1));
	}

	@Override
	public ResourceLocation getUid(){
		return ID;
	}

	@Override
	public Class<? extends MillRec> getRecipeClass(){
		return MillRec.class;
	}

	@Override
	public String getTitle(){
		return CRBlocks.millstone.getName().getString();
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public IDrawable getIcon(){
		return icon;
	}

	@Override
	public void setIngredients(MillRec recipe, IIngredients ingredients){
		ingredients.setInputIngredients(recipe.getIngredients());
		ingredients.setOutputs(VanillaTypes.ITEM, (ArrayList<ItemStack>) Lists.newArrayList(recipe.getOutputs()));
	}

	@Override
	public void draw(MillRec recipe, PoseStack matrix, double mouseX, double mouseY){
//		GlStateManager.enableAlpha();
//		GlStateManager.enableBlend();
		slot.draw(matrix, 79, 16);
		slot.draw(matrix, 61, 52);
		slot.draw(matrix, 79, 52);
		slot.draw(matrix, 97, 52);
		arrowStatic.draw(matrix, 66, 35);
		arrow.draw(matrix, 66, 35);
//		GlStateManager.disableBlend();
//		GlStateManager.disableAlpha();
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, MillRec recipe, IIngredients ingredients){
		IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
		itemStackGroup.init(0, true, 79, 16);
//		itemStackGroup.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));

		int length = recipe.getOutputs().length;
		if(length >= 1){
			itemStackGroup.init(1, false, 61, 52);
//			itemStackGroup.set(1, recipe.getOutputs()[0]);
			if(length >= 2){
				itemStackGroup.init(2, false, 79, 52);
//				itemStackGroup.set(2, recipe.getOutputs()[1]);
				if(length >= 3){
					itemStackGroup.init(3, false, 97, 52);
//					itemStackGroup.set(3, recipe.getOutputs()[2]);
				}
			}
		}

		itemStackGroup.set(ingredients);
	}
}

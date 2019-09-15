package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.google.common.collect.ImmutableList;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class MillstoneCategory implements IRecipeCategory<MillstoneRecipe>{

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
		icon = guiHelper.createDrawableIngredient(new ItemStack(CrossroadsBlocks.millstone, 1));
	}

	@Override
	public ResourceLocation getUid(){
		return ID;
	}

	@Override
	public Class<? extends MillstoneRecipe> getRecipeClass(){
		return MillstoneRecipe.class;
	}

	@Override
	public String getTitle(){
		return CrossroadsBlocks.millstone.getNameTextComponent().getFormattedText();
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
	public void setIngredients(MillstoneRecipe millstoneRecipe, IIngredients ingredients){
		ingredients.setInputLists(VanillaTypes.ITEM, ImmutableList.of(millstoneRecipe.inputs));
		ingredients.setOutputs(VanillaTypes.ITEM, millstoneRecipe.outputs);
	}

	@Override
	public void draw(MillstoneRecipe recipe, double mouseX, double mouseY){
//		GlStateManager.enableAlpha();
//		GlStateManager.enableBlend();
		slot.draw(79, 16);
		slot.draw(61, 52);
		slot.draw(79, 52);
		slot.draw(97, 52);
		arrowStatic.draw(66, 35);
		arrow.draw(66, 35);
//		GlStateManager.disableBlend();
//		GlStateManager.disableAlpha();
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, MillstoneRecipe recipeWrapper, IIngredients ingredients){
		IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
		itemStackGroup.init(0, true, 79, 16);
		itemStackGroup.set(0, recipeWrapper.inputs);

		itemStackGroup.init(1, false, 61, 52);
		itemStackGroup.init(2, false, 79, 52);
		itemStackGroup.init(3, false, 97, 52);
		itemStackGroup.set(1, recipeWrapper.outputs.size() >= 1 ? recipeWrapper.outputs.get(0) : null);
		itemStackGroup.set(2, recipeWrapper.outputs.size() >= 2 ? recipeWrapper.outputs.get(1) : null);
		itemStackGroup.set(3, recipeWrapper.outputs.size() == 3 ? recipeWrapper.outputs.get(2) : null);

		itemStackGroup.set(ingredients);
	}
}

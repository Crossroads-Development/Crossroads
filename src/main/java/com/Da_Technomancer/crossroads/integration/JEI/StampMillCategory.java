package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.recipes.StampMillRec;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class StampMillCategory implements IRecipeCategory<StampMillRec>{

	public static final ResourceLocation ID = new ResourceLocation(Crossroads.MODID, "stamp_mill");
	private final IDrawable back;
	private final IDrawable slot;
	private final IDrawableAnimated arrow;
	private final IDrawableStatic arrowStatic;
	private final IDrawable icon;

	protected StampMillCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		slot = guiHelper.getSlotDrawable();
		arrowStatic = guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 79, 35, 24, 17);
		arrow = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 176, 14, 24, 17), 40, IDrawableAnimated.StartDirection.LEFT, false);
		icon = guiHelper.createDrawableIngredient(new ItemStack(CRBlocks.stampMill, 1));
	}

	@Override
	public ResourceLocation getUid(){
		return ID;
	}

	@Override
	public Class<? extends StampMillRec> getRecipeClass(){
		return StampMillRec.class;
	}

	@Override
	public Component getTitle(){
		return CRBlocks.stampMill.getName();
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
	public void draw(StampMillRec recipe, PoseStack matrix, double mouseX, double mouseY){
//		GlStateManager.enableAlpha();
//		GlStateManager.enableBlend();
		slot.draw(matrix, 54, 50);
		slot.draw(matrix, 110, 50);
		arrowStatic.draw(matrix, 78, 50);
		arrow.draw(matrix, 78, 50);
//		GlStateManager.disableBlend();
//		GlStateManager.disableAlpha();
	}

	@Override
	public void setRecipe(IRecipeLayout layout, StampMillRec recipe, IIngredients ingredients){
		IGuiItemStackGroup itemGroup = layout.getItemStacks();

		itemGroup.init(0, true, 54, 50);
//		itemGroup.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));

		itemGroup.init(1, false, 110, 50);
//		itemGroup.set(1, recipe.getResultItem());

		itemGroup.set(ingredients);
	}

	@Override
	public void setIngredients(StampMillRec recipe, IIngredients ingredients){
		ingredients.setInputIngredients(recipe.getIngredients());
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
	}
}

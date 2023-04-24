package com.Da_Technomancer.crossroads.integration.jei;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.IncubatorRec;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class IncubatorCategory implements IRecipeCategory<IncubatorRec>{

	public static final RecipeType<IncubatorRec> TYPE = RecipeType.create(Crossroads.MODID, "incubator", IncubatorRec.class);
	private final IDrawable back;
	private final IDrawable slot;
	private final IDrawable icon;
	private final IDrawableAnimated arrow;
	private final IDrawableStatic arrowStatic;

	protected IncubatorCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		slot = guiHelper.getSlotDrawable();
		arrowStatic = guiHelper.createDrawable(new ResourceLocation(Crossroads.MODID, "textures/gui/container/incubator_gui.png"), 43, 35, 54, 10);
		arrow = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(new ResourceLocation(Crossroads.MODID, "textures/gui/container/incubator_gui.png"), 176, 0, 54, 10), 40, IDrawableAnimated.StartDirection.LEFT, false);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CRBlocks.incubator, 1));
	}

	@Override
	public RecipeType<IncubatorRec> getRecipeType(){
		return TYPE;
	}

	@Override
	public Component getTitle(){
		return CRBlocks.incubator.getName();
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
	public void draw(IncubatorRec recipe, IRecipeSlotsView view, PoseStack matrix, double mouseX, double mouseY){
//		GlStateManager.enableAlpha();
//		GlStateManager.enableBlend();
		slot.draw(matrix, 26, 23);
		slot.draw(matrix, 26, 41);
		slot.draw(matrix, 98, 32);
		arrowStatic.draw(matrix, 43, 35);
		arrow.draw(matrix, 43, 35);
//		GlStateManager.disableBlend();
//		GlStateManager.disableAlpha();
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, IncubatorRec recipe, IFocusGroup focuses){
		builder.addSlot(RecipeIngredientRole.INPUT, 26, 23).addIngredients(recipe.getMainInput());
		builder.addSlot(RecipeIngredientRole.INPUT, 26, 41).addIngredients(recipe.getSecondaryInput());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 98, 32).addItemStack(recipe.getResultItem());
	}
}

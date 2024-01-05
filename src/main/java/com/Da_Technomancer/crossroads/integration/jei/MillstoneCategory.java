package com.Da_Technomancer.crossroads.integration.jei;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.MillRec;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class MillstoneCategory implements IRecipeCategory<MillRec>{

	public static final RecipeType<MillRec> TYPE = RecipeType.create(Crossroads.MODID, "millstone", MillRec.class);
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
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CRBlocks.millstone, 1));
	}

	@Override
	public RecipeType<MillRec> getRecipeType(){
		return TYPE;
	}

	@Override
	public Component getTitle(){
		return CRBlocks.millstone.getName();
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
	public void draw(MillRec recipe, IRecipeSlotsView view, GuiGraphics matrix, double mouseX, double mouseY){
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
	public void setRecipe(IRecipeLayoutBuilder builder, MillRec recipe, IFocusGroup focuses){
		builder.addSlot(RecipeIngredientRole.INPUT, 80, 17).addIngredients(recipe.getIngredient());
		int length = recipe.getOutputs().length;
		if(length >= 1){
			builder.addSlot(RecipeIngredientRole.OUTPUT, 62, 53).addItemStack(recipe.getOutputs()[0]);
			if(length >= 2){
				builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 53).addItemStack(recipe.getOutputs()[1]);
				if(length >= 3){
					builder.addSlot(RecipeIngredientRole.OUTPUT, 98, 53).addItemStack(recipe.getOutputs()[2]);
				}
			}
		}
	}
}

package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.recipes.CentrifugeRec;
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

public class CentrifugeCategory implements IRecipeCategory<CentrifugeRec>{

	public static final RecipeType<CentrifugeRec> TYPE = RecipeType.create(Crossroads.MODID, "centrifuge", CentrifugeRec.class);
	private final IDrawable back;
	private final IDrawable icon;
	private final IDrawable slot;
	private final IDrawableAnimated arrow;
	private final IDrawableStatic arrowStatic;
	private final IDrawable fluidOverlay;

	protected CentrifugeCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		slot = guiHelper.getSlotDrawable();
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(CRBlocks.waterCentrifuge, 1));
		arrowStatic = guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 79, 35, 24, 17);
		arrow = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 176, 14, 24, 17), 40, IDrawableAnimated.StartDirection.LEFT, false);
		fluidOverlay = JEICrossroadsPlugin.createFluidOverlay(guiHelper);
	}

	@Override
	public ResourceLocation getUid(){
		return TYPE.getUid();
	}

	@Override
	public Class<? extends CentrifugeRec> getRecipeClass(){
		return TYPE.getRecipeClass();
	}

	@Override
	public RecipeType<CentrifugeRec> getRecipeType(){
		return TYPE;
	}

	@Override
	public Component getTitle(){
		return CRBlocks.waterCentrifuge.getName();
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void draw(CentrifugeRec rec, IRecipeSlotsView view, PoseStack matrix, double mouseX, double mouseY){
		arrowStatic.draw(matrix, 75, 56);
		arrow.draw(matrix, 75, 56);
		slot.draw(matrix, 130, 30);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CentrifugeRec recipe, IFocusGroup focuses){
		builder.addSlot(RecipeIngredientRole.INPUT, 51, 31).addIngredient(VanillaTypes.FLUID, recipe.getInput()).setFluidRenderer(4000, true, 16, 32).setOverlay(fluidOverlay, 0, 0);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 111, 31).addIngredient(VanillaTypes.FLUID, recipe.getFluidOutput()).setFluidRenderer(4000, true, 16, 32).setOverlay(fluidOverlay, 0, 0);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 131, 31).addItemStacks(recipe.getOutputList());
	}

	@Override
	public IDrawable getIcon(){
		return icon;
	}
}

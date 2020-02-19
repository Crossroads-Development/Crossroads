package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.crafting.recipes.CentrifugeRec;
import com.google.common.collect.ImmutableList;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CentrifugeCategory implements IRecipeCategory<CentrifugeRec>{

	public static final ResourceLocation ID = new ResourceLocation(Crossroads.MODID, "centrifuge");
	private final IDrawable back;
	private final IDrawable icon;
	private final IDrawable slot;
	private final IDrawableAnimated arrow;
	private final IDrawableStatic arrowStatic;
	private final IDrawable fluidOverlay;

	protected CentrifugeCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		slot = guiHelper.getSlotDrawable();
		icon = guiHelper.createDrawableIngredient(new ItemStack(CRBlocks.waterCentrifuge, 1));
		arrowStatic = guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 79, 35, 24, 17);
		arrow = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 176, 14, 24, 17), 40, IDrawableAnimated.StartDirection.LEFT, false);
		fluidOverlay = JEICrossroadsPlugin.createFluidOverlay(guiHelper);
	}

	@Override
	public ResourceLocation getUid(){
		return ID;
	}

	@Override
	public Class<? extends CentrifugeRec> getRecipeClass(){
		return CentrifugeRec.class;
	}

	@Override
	public String getTitle(){
		return CRBlocks.waterCentrifuge.getNameTextComponent().getFormattedText();
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void draw(CentrifugeRec rec, double mouseX, double mouseY){
//		GlStateManager.enableAlpha();
//		GlStateManager.enableBlend();
		arrowStatic.draw(75, 56);
		arrow.draw(75, 56);
		slot.draw(130, 30);
//		GlStateManager.disableBlend();
//		GlStateManager.disableAlpha();
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CentrifugeRec recipe, IIngredients ingredients){
		IGuiItemStackGroup itemGroup = recipeLayout.getItemStacks();
		IGuiFluidStackGroup fluidGroup = recipeLayout.getFluidStacks();

		itemGroup.init(0, false, 130, 30);
		itemGroup.set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));

		fluidGroup.init(0, true, 50, 30, 16, 64, 4000, true, fluidOverlay);
		fluidGroup.set(0, recipe.getInput());
		fluidGroup.init(1, false, 110, 30, 16, 64, 4000, true, fluidOverlay);
		fluidGroup.set(1, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
	}

	@Override
	public IDrawable getIcon(){
		return icon;
	}

	@Override
	public void setIngredients(CentrifugeRec recipe, IIngredients ingredients){
		ingredients.setInput(VanillaTypes.FLUID, recipe.getInput());
		ingredients.setOutput(VanillaTypes.FLUID, recipe.getFluidOutput());
		ingredients.setOutputLists(VanillaTypes.ITEM, ImmutableList.of(recipe.getOutputList()));
	}
}

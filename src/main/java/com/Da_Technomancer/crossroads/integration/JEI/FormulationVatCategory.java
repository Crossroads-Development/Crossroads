package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.recipes.FormulationVatRec;
import com.mojang.blaze3d.matrix.MatrixStack;
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

public class FormulationVatCategory implements IRecipeCategory<FormulationVatRec>{

	public static final ResourceLocation ID = new ResourceLocation(Crossroads.MODID, "formulation_vat");
	private final IDrawable back;
	private final IDrawable icon;
	private final IDrawable slot;
	private final IDrawableAnimated arrow;
	private final IDrawableStatic arrowStatic;
	private final IDrawable fluidOverlay;

	protected FormulationVatCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		icon = guiHelper.createDrawableIngredient(new ItemStack(CRBlocks.formulationVat, 1));
		slot = guiHelper.getSlotDrawable();
		arrowStatic = guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 79, 35, 24, 17);
		arrow = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 176, 14, 24, 17), 40, IDrawableAnimated.StartDirection.LEFT, false);
		fluidOverlay = JEICrossroadsPlugin.createFluidOverlay(guiHelper);
	}

	@Override
	public ResourceLocation getUid(){
		return ID;
	}

	@Override
	public Class<? extends FormulationVatRec> getRecipeClass(){
		return FormulationVatRec.class;
	}

	@Override
	public String getTitle(){
		return CRBlocks.formulationVat.getName().getString();
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void draw(FormulationVatRec rec, MatrixStack matrix, double mouseX, double mouseY){
//		GlStateManager.enableAlpha();
//		GlStateManager.enableBlend();
		slot.draw(matrix, 50, 55);
		arrowStatic.draw(matrix, 75, 56);
		arrow.draw(matrix, 75, 56);
//		GlStateManager.disableBlend();
//		GlStateManager.disableAlpha();
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, FormulationVatRec recipe, IIngredients ingredients){
		IGuiItemStackGroup itemGroup = recipeLayout.getItemStacks();
		IGuiFluidStackGroup fluidGroup = recipeLayout.getFluidStacks();

		fluidGroup.init(0, true, 30, 30, 16, 64, 4000, true, fluidOverlay);
		itemGroup.init(0, true, 50, 55);
		fluidGroup.init(1, false, 110, 30, 16, 64, 4000, true, fluidOverlay);

		itemGroup.set(ingredients);
		fluidGroup.set(ingredients);
	}

	@Override
	public IDrawable getIcon(){
		return icon;
	}

	@Override
	public void setIngredients(FormulationVatRec rec, IIngredients ingredients){
		ingredients.setInput(VanillaTypes.FLUID, rec.getInput());
		ingredients.setInputIngredients(rec.getIngredients());
		ingredients.setOutput(VanillaTypes.FLUID, rec.getOutput());
	}
}

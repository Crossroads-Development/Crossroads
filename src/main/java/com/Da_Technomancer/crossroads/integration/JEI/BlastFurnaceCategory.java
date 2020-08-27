package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.crafting.recipes.BlastFurnaceRec;
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
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class BlastFurnaceCategory implements IRecipeCategory<BlastFurnaceRec>{

	public static final ResourceLocation ID = new ResourceLocation(Crossroads.MODID, "blast_furnace");
	private final IDrawable back;
	private final IDrawable slot;
	private final IDrawableAnimated arrow;
	private final IDrawableStatic arrowStatic;
	private final IDrawable fluidOverlay;
	private final IDrawable icon;

	protected BlastFurnaceCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		slot = guiHelper.getSlotDrawable();
		arrowStatic = guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 79, 35, 24, 17);
		arrow = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 176, 14, 24, 17), 40, IDrawableAnimated.StartDirection.LEFT, false);
		fluidOverlay = JEICrossroadsPlugin.createFluidOverlay(guiHelper);
		icon = guiHelper.createDrawableIngredient(new ItemStack(CRBlocks.blastFurnace, 1));
	}

	@Override
	public ResourceLocation getUid(){
		return ID;
	}

	@Override
	public Class<? extends BlastFurnaceRec> getRecipeClass(){
		return BlastFurnaceRec.class;
	}

	@Override
	public String getTitle(){
		return CRBlocks.blastFurnace.getTranslatedName().getString();
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void draw(BlastFurnaceRec recipe, MatrixStack matrix, double mouseX, double mouseY){
//		GlStateManager.enableAlpha();
//		GlStateManager.enableBlend();
		slot.draw(matrix, 54, 55);//Input
		slot.draw(matrix, 130, 55);//Slag
		arrowStatic.draw(matrix, 78, 55);
		arrow.draw(matrix, 78, 55);
//		GlStateManager.disableBlend();
//		GlStateManager.disableAlpha();
		Minecraft.getInstance().fontRenderer.drawString(matrix, MiscUtil.localize("crossroads.jei.blast_furnace.carbon", recipe.getSlag()), 10, 10, 0x404040);
	}

	@Override
	public void setRecipe(IRecipeLayout layout, BlastFurnaceRec recipe, IIngredients ingredients){
		IGuiItemStackGroup itemGroup = layout.getItemStacks();
		IGuiFluidStackGroup fluidGroup = layout.getFluidStacks();

		itemGroup.init(0, true, 54, 55);
		itemGroup.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
		itemGroup.init(1, false, 130, 55);
		itemGroup.set(1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
		fluidGroup.init(0, false, 110, 22, 16, 64, 1_000, true, fluidOverlay);
		fluidGroup.set(0, ingredients.getOutputs(VanillaTypes.FLUID).get(0));

		itemGroup.set(ingredients);
		fluidGroup.set(ingredients);
	}

	@Override
	public IDrawable getIcon(){
		return icon;
	}

	@Override
	public void setIngredients(BlastFurnaceRec recipe, IIngredients ingredients){
		ingredients.setInputIngredients(recipe.getIngredients());
		ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutput());
		ingredients.setOutput(VanillaTypes.ITEM, new ItemStack(CRItems.slag, recipe.getSlag()));
	}
}

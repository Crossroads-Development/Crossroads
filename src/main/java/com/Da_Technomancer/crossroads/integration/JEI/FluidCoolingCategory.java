package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
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

public class FluidCoolingCategory implements IRecipeCategory<FluidCoolingRecipe>{

	public static final ResourceLocation ID = new ResourceLocation(Crossroads.MODID, "fluid_cooling");
	private final IDrawable back;
	private final IDrawable icon;
	private final IDrawable slot;
	private final IDrawableAnimated arrow;
	private final IDrawableStatic arrowStatic;
	private final IDrawable fluidOverlay;

	protected FluidCoolingCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		icon = guiHelper.createDrawableIngredient(new ItemStack(CrossroadsBlocks.fluidCoolingChamber, 1));
		slot = guiHelper.getSlotDrawable();
		arrowStatic = guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 79, 35, 24, 17);
		arrow = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 176, 14, 24, 17), 40, IDrawableAnimated.StartDirection.LEFT, false);
		fluidOverlay = guiHelper.createDrawable(new ResourceLocation(Crossroads.MODID, "textures/gui/rectangle_fluid_overlay.png"), 0, 0, 16, 64);
	}

	@Override
	public ResourceLocation getUid(){
		return ID;
	}

	@Override
	public Class<? extends FluidCoolingRecipe> getRecipeClass(){
		return FluidCoolingRecipe.class;
	}

	@Override
	public String getTitle(){
		return CrossroadsBlocks.fluidCoolingChamber.getNameTextComponent().getFormattedText();
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void draw(FluidCoolingRecipe rec, double mouseX, double mouseY){
		Minecraft.getInstance().fontRenderer.drawString("Maximum temp: " + rec.max + "°C", 10, 10, 4210752);
		Minecraft.getInstance().fontRenderer.drawString("Heat Added: " + rec.add + "°C", 10, 20, 4210752);
//		GlStateManager.enableAlpha();
//		GlStateManager.enableBlend();
		slot.draw(80, 55);
		arrowStatic.draw(45, 56);
		arrow.draw(45, 56);
//		GlStateManager.disableBlend();
//		GlStateManager.disableAlpha();
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, FluidCoolingRecipe recipeWrapper, IIngredients ingredients){
		IGuiItemStackGroup itemGroup = recipeLayout.getItemStacks();
		IGuiFluidStackGroup fluidGroup = recipeLayout.getFluidStacks();

		fluidGroup.init(0, true, 21, 30, 16, 64, 1000, true, fluidOverlay);
		fluidGroup.set(0, recipeWrapper.fluid);
		itemGroup.init(0, false, 80, 55);
		itemGroup.set(0, recipeWrapper.stack);

		itemGroup.set(ingredients);
		fluidGroup.set(ingredients);
	}

	@Override
	public IDrawable getIcon(){
		return icon;
	}

	@Override
	public void setIngredients(FluidCoolingRecipe fluidCoolingRecipe, IIngredients ingredients){
		ingredients.setInput(VanillaTypes.FLUID, fluidCoolingRecipe.fluid);
		ingredients.setOutput(VanillaTypes.ITEM, fluidCoolingRecipe.stack);
	}
}

package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.recipes.FluidCoolingRec;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

public class FluidCoolingCategory implements IRecipeCategory<FluidCoolingRec>{

	public static final ResourceLocation ID = new ResourceLocation(Crossroads.MODID, "fluid_cooling");
	private final IDrawable back;
	private final IDrawable icon;
	private final IDrawable slot;
	private final IDrawableAnimated arrow;
	private final IDrawableStatic arrowStatic;
	private final IDrawable fluidOverlay;

	protected FluidCoolingCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		icon = guiHelper.createDrawableIngredient(new ItemStack(CRBlocks.fluidCoolingChamber, 1));
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
	public Class<? extends FluidCoolingRec> getRecipeClass(){
		return FluidCoolingRec.class;
	}

	@Override
	public Component getTitle(){
		return CRBlocks.fluidCoolingChamber.getName();
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void draw(FluidCoolingRec rec, PoseStack matrix, double mouseX, double mouseY){
		Minecraft.getInstance().font.draw(matrix, MiscUtil.localize("crossroads.jei.fluid_cooling.max", rec.getMaxTemp()), 10, 10, 4210752);
		Minecraft.getInstance().font.draw(matrix, MiscUtil.localize("crossroads.jei.fluid_cooling.add", rec.getAddedHeat()), 10, 20, 4210752);
//		GlStateManager.enableAlpha();
//		GlStateManager.enableBlend();
		slot.draw(matrix, 110, 55);
		arrowStatic.draw(matrix, 75, 56);
		arrow.draw(matrix, 75, 56);
//		GlStateManager.disableBlend();
//		GlStateManager.disableAlpha();
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, FluidCoolingRec recipe, IIngredients ingredients){
		IGuiItemStackGroup itemGroup = recipeLayout.getItemStacks();
		IGuiFluidStackGroup fluidGroup = recipeLayout.getFluidStacks();

		fluidGroup.init(0, true, 50, 30, 16, 64, 1000, true, fluidOverlay);
//		fluidGroup.set(0, recipe.getInput());
		itemGroup.init(0, false, 110, 55);
//		itemGroup.set(0, recipe.getResultItem());

		itemGroup.set(ingredients);
		fluidGroup.set(ingredients);
	}

	@Override
	public IDrawable getIcon(){
		return icon;
	}

	@Override
	public void setIngredients(FluidCoolingRec fluidCoolingRecipe, IIngredients ingredients){
		ingredients.setInputLists(VanillaTypes.FLUID, ImmutableList.of(fluidCoolingRecipe.getInput().getMatchedFluidStacks(fluidCoolingRecipe.getInputQty())));
		ingredients.setOutput(VanillaTypes.ITEM, fluidCoolingRecipe.getResultItem());
	}
}

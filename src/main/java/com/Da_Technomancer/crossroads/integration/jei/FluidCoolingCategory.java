package com.Da_Technomancer.crossroads.integration.jei;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.FluidCoolingRec;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
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
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class FluidCoolingCategory implements IRecipeCategory<FluidCoolingRec>{

	public static final RecipeType<FluidCoolingRec> TYPE = RecipeType.create(Crossroads.MODID, "fluid_cooling", FluidCoolingRec.class);
	private final IDrawable back;
	private final IDrawable icon;
	private final IDrawable slot;
	private final IDrawableAnimated arrow;
	private final IDrawableStatic arrowStatic;
	private final IDrawable fluidOverlay;

	protected FluidCoolingCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CRBlocks.fluidCoolingChamber, 1));
		slot = guiHelper.getSlotDrawable();
		arrowStatic = guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 79, 35, 24, 17);
		arrow = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 176, 14, 24, 17), 40, IDrawableAnimated.StartDirection.LEFT, false);
		fluidOverlay = JEICrossroadsPlugin.createFluidOverlay(guiHelper);
	}

	@Override
	public RecipeType<FluidCoolingRec> getRecipeType(){
		return TYPE;
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
	public void draw(FluidCoolingRec rec, IRecipeSlotsView view, PoseStack matrix, double mouseX, double mouseY){
		Minecraft.getInstance().font.draw(matrix, MiscUtil.localize("crossroads.jei.fluid_cooling.max", rec.getMaxTemp()), 10, 10, 4210752);
		Minecraft.getInstance().font.draw(matrix, MiscUtil.localize("crossroads.jei.fluid_cooling.add", rec.getAddedHeat()), 10, 20, 4210752);
		slot.draw(matrix, 110, 55);
		arrowStatic.draw(matrix, 75, 56);
		arrow.draw(matrix, 75, 56);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, FluidCoolingRec recipe, IFocusGroup focuses){
		builder.addSlot(RecipeIngredientRole.INPUT, 51, 31).addIngredients(ForgeTypes.FLUID_STACK, recipe.getInput().getMatchedFluidStacks(recipe.getInputQty())).setFluidRenderer(1000, true, 16, 64).setOverlay(fluidOverlay, 0, 0);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 111, 56).addItemStack(recipe.getResultItem());
	}

	@Override
	public IDrawable getIcon(){
		return icon;
	}
}

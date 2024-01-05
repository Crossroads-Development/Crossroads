package com.Da_Technomancer.crossroads.integration.jei;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.FormulationVatRec;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class FormulationVatCategory implements IRecipeCategory<FormulationVatRec>{

	public static final RecipeType<FormulationVatRec> TYPE = RecipeType.create(Crossroads.MODID, "formulation_vat", FormulationVatRec.class);
	private final IDrawable back;
	private final IDrawable icon;
	private final IDrawable slot;
	private final IDrawableAnimated arrow;
	private final IDrawableStatic arrowStatic;
	private final IDrawable fluidOverlay;

	protected FormulationVatCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CRBlocks.formulationVat, 1));
		slot = guiHelper.getSlotDrawable();
		arrowStatic = guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 79, 35, 24, 17);
		arrow = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 176, 14, 24, 17), 40, IDrawableAnimated.StartDirection.LEFT, false);
		fluidOverlay = JEICrossroadsPlugin.createFluidOverlay(guiHelper);
	}

	@Override
	public RecipeType<FormulationVatRec> getRecipeType(){
		return TYPE;
	}

	@Override
	public Component getTitle(){
		return CRBlocks.formulationVat.getName();
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void draw(FormulationVatRec rec, IRecipeSlotsView view, GuiGraphics matrix, double mouseX, double mouseY){
		slot.draw(matrix, 50, 55);
		arrowStatic.draw(matrix, 75, 56);
		arrow.draw(matrix, 75, 56);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, FormulationVatRec recipe, IFocusGroup focuses){
		builder.addSlot(RecipeIngredientRole.INPUT, 31, 31).addIngredients(ForgeTypes.FLUID_STACK, recipe.getInput().getMatchedFluidStacks(recipe.getInputQty())).setFluidRenderer(4000, true, 16, 64).setOverlay(fluidOverlay, 0, 0);
		builder.addSlot(RecipeIngredientRole.INPUT, 51, 56).addIngredients(recipe.getIngredient());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 111, 31).addIngredient(ForgeTypes.FLUID_STACK, recipe.getOutput()).setFluidRenderer(4000, true, 16, 64).setOverlay(fluidOverlay, 0, 0);
	}

	@Override
	public IDrawable getIcon(){
		return icon;
	}
}

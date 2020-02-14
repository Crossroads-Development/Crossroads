package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.crafting.recipes.CrucibleRec;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class HeatingCrucibleCategory implements IRecipeCategory<CrucibleRec>{

	public static final ResourceLocation ID = new ResourceLocation(Crossroads.MODID, "heating_crucible");
	private final IDrawable back;
	private final IDrawable slot;
	private final IDrawableAnimated arrow;
	private final IDrawable icon;
	private final IDrawable arrowStatic;
	private final IDrawable fluidOverlay;

	protected HeatingCrucibleCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		slot = guiHelper.getSlotDrawable();

		arrowStatic = guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 79, 35, 24, 17);
		arrow = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 176, 14, 24, 17), 40, IDrawableAnimated.StartDirection.LEFT, false);
		icon = guiHelper.createDrawableIngredient(new ItemStack(CRBlocks.heatingCrucible, 1));
		fluidOverlay = guiHelper.createDrawable(new ResourceLocation(Crossroads.MODID, "textures/gui/square_fluid_overlay.png"), 0, 0, 32, 32);
	}

	@Override
	public ResourceLocation getUid(){
		return ID;
	}

	@Override
	public Class<? extends CrucibleRec> getRecipeClass(){
		return CrucibleRec.class;
	}

	@Override
	public String getTitle(){
		return CRBlocks.heatingCrucible.getNameTextComponent().getFormattedText();
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void draw(CrucibleRec recipe, double mouseX, double mouseY){
//		GlStateManager.enableAlpha();
//		GlStateManager.enableBlend();
		Minecraft.getInstance().fontRenderer.drawString("When above 1000°C", 10, 10, 4210752);
		slot.draw(40, 50);
		arrowStatic.draw(62, 50);
		arrow.draw(62, 50);
//		GlStateManager.disableBlend();
//		GlStateManager.disableAlpha();
	}

	@Override
	public void setRecipe(IRecipeLayout layout, CrucibleRec recipe, IIngredients ingredients){
		IGuiItemStackGroup itemGroup = layout.getItemStacks();
		IGuiFluidStackGroup fluidGroup = layout.getFluidStacks();

//		List<FluidStack> fluids = ingredients.getOutputs(VanillaTypes.FLUID).get(0);
		fluidGroup.init(0, false, 90, 42, 32, 32, recipe.getOutput().getAmount(), false, fluidOverlay);
		fluidGroup.set(0, recipe.getOutput());
		itemGroup.init(0, true, 40, 50);
		itemGroup.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
	}

	@Override
	public IDrawable getIcon(){
		return icon;
	}

	@Override
	public void setIngredients(CrucibleRec recipe, IIngredients ingredients){
		ingredients.setInputIngredients(recipe.getIngredients());
		ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutput());
	}
}

package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;


public class BeamExtractorCategory implements IRecipeCategory<BeamExtractorRecipe>{

	public static final ResourceLocation ID = new ResourceLocation(Crossroads.MODID, "beam_extractor");
	private final IDrawable back;
	private final IDrawable slot;
	private final IDrawable arrowStatic;
	private final IDrawable icon;

	protected BeamExtractorCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		slot = guiHelper.getSlotDrawable();
		icon = guiHelper.createDrawableIngredient(new ItemStack(CrossroadsBlocks.beamExtractor, 1));
		arrowStatic = guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 79, 35, 24, 17);
	}

	@Override
	public ResourceLocation getUid(){
		return ID;
	}

	@Override
	public Class<? extends BeamExtractorRecipe> getRecipeClass(){
		return BeamExtractorRecipe.class;
	}

	@Override
	public String getTitle(){
		return CrossroadsBlocks.beamExtractor.getNameTextComponent().getFormattedText();
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
	public void setIngredients(BeamExtractorRecipe recipe, IIngredients ingredients){
		ingredients.setInput(VanillaTypes.ITEM, recipe.in);
	}

	@Override
	public void draw(BeamExtractorRecipe rec, double mouseX, double mouseY){
//		GlStateManager.enableAlpha();
//		GlStateManager.enableBlend();
		slot.draw(20, 50);
		arrowStatic.draw(46, 50);
//		GlStateManager.disableBlend();
//		GlStateManager.disableAlpha();

		Minecraft minecraft = Minecraft.getInstance();
		minecraft.fontRenderer.drawString("Energy: " + rec.out.getEnergy(), 80, 25, 0x404040);
		minecraft.fontRenderer.drawString("Potential: " + rec.out.getPotential(), 80, 45, 0x404040);
		minecraft.fontRenderer.drawString("Stability: " + rec.out.getStability(), 80, 65, 0x404040);
		minecraft.fontRenderer.drawString("Void: " + rec.out.getVoid(), 80, 85, 0x404040);
	}

	@Override
	public void setRecipe(IRecipeLayout layout, BeamExtractorRecipe recipe, IIngredients ingredients){
		IGuiItemStackGroup itemGroup = layout.getItemStacks();
		itemGroup.init(0, true, 20, 50);
		itemGroup.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
	}
}

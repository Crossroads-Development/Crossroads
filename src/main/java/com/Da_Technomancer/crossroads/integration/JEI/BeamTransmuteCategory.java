package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.crafting.recipes.BeamTransmuteRec;
import com.google.common.collect.ImmutableList;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class BeamTransmuteCategory implements IRecipeCategory<BeamTransmuteRec>{

	public static final ResourceLocation ID = new ResourceLocation(Crossroads.MODID, "transmute_beam");
	private final IDrawable back;
	private final IDrawable slot;
	private final IDrawable arrowStatic;
	private final IDrawable icon;

	protected BeamTransmuteCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 80);
		slot = guiHelper.getSlotDrawable();
		icon = guiHelper.createDrawableIngredient(new ItemStack(CRBlocks.beamReflector, 1));

		arrowStatic = guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 79, 35, 24, 17);
	}

	@Override
	public ResourceLocation getUid(){
		return ID;
	}

	@Override
	public Class<? extends BeamTransmuteRec> getRecipeClass(){
		return BeamTransmuteRec.class;
	}

	@Override
	public String getTitle(){
		return "Beam Transmutation";
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
	public void draw(BeamTransmuteRec recipe, double mouseX, double mouseY){
//		GlStateManager.enableAlpha();
//		GlStateManager.enableBlend();
		slot.draw(40, 40);//Input
		slot.draw(120, 40);//Output
		arrowStatic.draw(78, 40);
		Minecraft.getInstance().fontRenderer.drawString(MiscUtil.localize("crossroads.jei.beam_trans.align", recipe.getAlign().getLocalName(recipe.isVoid())), 50, 70, 0x404040);
		Minecraft.getInstance().fontRenderer.drawString(MiscUtil.localize("crossroads.jei.beam_trans.power", recipe.getPower()), 50, 90, 0x404040);
//		GlStateManager.disableBlend();
//		GlStateManager.disableAlpha();
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, BeamTransmuteRec recipe, IIngredients ingredients){
		recipeLayout.getItemStacks().init(0, true, 40, 40);
		recipeLayout.getItemStacks().set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
		recipeLayout.getItemStacks().init(1, false, 120, 40);
		recipeLayout.getItemStacks().set(1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
	}

	@Override
	public void setIngredients(BeamTransmuteRec recipe, IIngredients ingredients){
		//Strictly speaking, the 'correct' way to do this is to register a new ingredient type of block, but meh
		//TODO this really should be done the correct way- lots of blocks don't map to items cleanly
		ingredients.setInputLists(VanillaTypes.ITEM, ImmutableList.of(recipe.getIngr().getMatchedItemForm()));
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
	}
}

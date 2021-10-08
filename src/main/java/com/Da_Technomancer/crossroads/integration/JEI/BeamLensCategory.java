package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.beams.BeamMod;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.recipes.BeamLensRec;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;
import net.minecraft.network.chat.TranslatableComponent;

public class BeamLensCategory implements IRecipeCategory<BeamLensRec>{

	public static final ResourceLocation ID = new ResourceLocation(Crossroads.MODID, "lens_beam");
	private final IDrawable back;
	private final IDrawable slot;
	private final IDrawable arrowStatic;
	private final IDrawable icon;

	protected BeamLensCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 80);
		slot = guiHelper.getSlotDrawable();
		icon = guiHelper.createDrawableIngredient(new ItemStack(CRBlocks.lensFrame, 1));

		arrowStatic = guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 79, 35, 24, 17);
	}

	@Override
	public ResourceLocation getUid(){
		return ID;
	}

	@Override
	public Class<? extends BeamLensRec> getRecipeClass(){
		return BeamLensRec.class;
	}

	@Override
	public String getTitle(){
		return "Lens Frame Interaction";
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
	public void draw(BeamLensRec recipe, PoseStack matrix, double mouseX, double mouseY){
//		GlStateManager.enableAlpha();
//		GlStateManager.enableBlend();
		slot.draw(matrix, 20, 15);//Input
		//Render without shadow

		// Draw relevant transmutation data
		if(recipe.getTransmuteAlignment() != EnumBeamAlignments.NO_MATCH) {
			String align = recipe.getTransmuteAlignment().getLocalName(recipe.isVoid());
			Minecraft.getInstance().font.draw(matrix, new TranslatableComponent("crossroads.jei.beam_trans.align", align), 2, 2, 0x404040);
			slot.draw(matrix, 20, 60);//Output

			// Rotate arrow to point downwards
			matrix.pushPose();
			matrix.translate(36, 35, 0);
			matrix.mulPose(Vector3f.ZN.rotationDegrees(-90));
			arrowStatic.draw(matrix, 0, 0);
			matrix.popPose();
		} else {
			Minecraft.getInstance().font.draw(matrix, new TranslatableComponent("crossroads.jei.beam_lens.no_trans"), 2, 2, 0x404040);
		}

		BeamMod output = recipe.getOutput();
		int energy = (int)(output.getEnergyMult() * 100);
		int potential = (int)(output.getPotentialMult() * 100);
		int stability = (int)(output.getStabilityMult() * 100);
		int voi = (int)(output.getVoidMult() * 100);
		int voidConv = (int)(output.getVoidConvert() * 100);

		int x = 50;
		int y = 18;
		int ySpacing = 15;

		if(output.isEmpty()) {
			Minecraft.getInstance().font.draw(matrix, new TranslatableComponent("crossroads.jei.beam_lens.no_change"), x, 37, 0x404040);
		} else {
			// Display all primary colors if any of them is changed
			if(energy != 100 || potential != 100 || stability != 100) {
				Minecraft.getInstance().font.draw(matrix, new TranslatableComponent("crossroads.jei.beam_lens.energy", energy), x, y, 0x404040);
				y += ySpacing;
				Minecraft.getInstance().font.draw(matrix, new TranslatableComponent("crossroads.jei.beam_lens.potential", potential), x, y, 0x404040);
				y += ySpacing;
				Minecraft.getInstance().font.draw(matrix, new TranslatableComponent("crossroads.jei.beam_lens.stability", stability), x, y, 0x404040);
				y += ySpacing;
			}
			// Display void if void is changed
			if(voi != 100) {
				Minecraft.getInstance().font.draw(matrix, new TranslatableComponent("crossroads.jei.beam_lens.void", voi), x, y, 0x404040);
				y += ySpacing;
			}
			// Display void conversion if conversion is taking place
			if(voidConv != 0) {
				Minecraft.getInstance().font.draw(matrix, new TranslatableComponent("crossroads.jei.beam_lens.void_convert", voidConv), x, y, 0x404040);
			}
		}

//		GlStateManager.disableBlend();
//		GlStateManager.disableAlpha();
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, BeamLensRec recipe, IIngredients ingredients){
		recipeLayout.getItemStacks().init(0, true, 20, 15);
		recipeLayout.getItemStacks().set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
		// Don't draw the second item if there is no valid transmutation
		if(recipe.getTransmuteAlignment() != EnumBeamAlignments.NO_MATCH) {
			recipeLayout.getItemStacks().init(1, false, 20, 60);
			recipeLayout.getItemStacks().set(1, recipe.getResultItem());
		}
	}

	@Override
	public void setIngredients(BeamLensRec recipe, IIngredients ingredients){
		ingredients.setInputIngredients(ImmutableList.of(recipe.getIngr()));
		if(recipe.getTransmuteAlignment() != EnumBeamAlignments.NO_MATCH) {
			ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
		}
	}
}

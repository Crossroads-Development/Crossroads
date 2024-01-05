package com.Da_Technomancer.crossroads.integration.jei;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.BeamTransmuteRec;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class BeamTransmuteCategory implements IRecipeCategory<BeamTransmuteRec>{

	public static final RecipeType<BeamTransmuteRec> TYPE = RecipeType.create(Crossroads.MODID, "transmute_beam", BeamTransmuteRec.class);
	private final IDrawable back;
	private final IDrawable slot;
	private final IDrawable arrowStatic;
	private final IDrawable icon;

	protected BeamTransmuteCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 80);
		slot = guiHelper.getSlotDrawable();
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CRBlocks.beamReflector, 1));

		arrowStatic = guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 79, 35, 24, 17);
	}

	@Override
	public RecipeType<BeamTransmuteRec> getRecipeType(){
		return TYPE;
	}

	@Override
	public Component getTitle(){
		return Component.translatable("crossroads.jei.beam_transmute.cat_name");
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
	public void draw(BeamTransmuteRec recipe, IRecipeSlotsView view, GuiGraphics matrix, double mouseX, double mouseY){
		slot.draw(matrix, 40, 40);//Input
		slot.draw(matrix, 120, 40);//Output
		arrowStatic.draw(matrix, 78, 40);
		//Render without shadow
		matrix.drawString(Minecraft.getInstance().font, Component.translatable("crossroads.jei.beam_trans.align", recipe.getAlign().getLocalName(recipe.isVoid())), 40, 10, 0x404040, false);
		matrix.drawString(Minecraft.getInstance().font, Component.translatable("crossroads.jei.beam_trans.power", recipe.getPower()), 40, 25, 0x404040, false);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, BeamTransmuteRec recipe, IFocusGroup focuses){
		//Strictly speaking, the 'correct' way to do this is to register a new ingredient type of block, but meh
		//TODO this really should be done the correct way- lots of blocks don't map to items cleanly
		builder.addSlot(RecipeIngredientRole.INPUT, 41, 41).addItemStacks(recipe.getIngr().getMatchedItemForm());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 121, 41).addItemStack(recipe.getResultItem());
	}
}

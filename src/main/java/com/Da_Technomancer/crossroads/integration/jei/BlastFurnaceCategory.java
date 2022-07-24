package com.Da_Technomancer.crossroads.integration.jei;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.BlastFurnaceRec;
import com.Da_Technomancer.crossroads.items.CRItems;
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

public class BlastFurnaceCategory implements IRecipeCategory<BlastFurnaceRec>{

	public static final RecipeType<BlastFurnaceRec> TYPE = RecipeType.create(Crossroads.MODID, "blast_furnace", BlastFurnaceRec.class);
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
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CRBlocks.blastFurnace, 1));
	}

	@Override
	public RecipeType<BlastFurnaceRec> getRecipeType(){
		return TYPE;
	}

	@Override
	public Component getTitle(){
		return CRBlocks.blastFurnace.getName();
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void draw(BlastFurnaceRec recipe, IRecipeSlotsView view, PoseStack matrix, double mouseX, double mouseY){
		slot.draw(matrix, 54, 55);//Input
		slot.draw(matrix, 130, 55);//Slag
		arrowStatic.draw(matrix, 78, 55);
		arrow.draw(matrix, 78, 55);
		Minecraft.getInstance().font.draw(matrix, MiscUtil.localize("crossroads.jei.blast_furnace.carbon", recipe.getSlag()), 10, 10, 0x404040);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, BlastFurnaceRec recipe, IFocusGroup focuses){
		builder.addSlot(RecipeIngredientRole.INPUT, 55, 56).addIngredients(recipe.getIngredient());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 131, 56).addItemStack(new ItemStack(CRItems.slag, recipe.getSlag()));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 111, 23).addIngredient(ForgeTypes.FLUID_STACK, recipe.getOutput()).setFluidRenderer(1000, true, 16, 64).setOverlay(fluidOverlay, 0, 0);
	}

	@Override
	public IDrawable getIcon(){
		return icon;
	}
}

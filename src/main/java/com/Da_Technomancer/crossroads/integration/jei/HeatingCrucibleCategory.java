package com.Da_Technomancer.crossroads.integration.jei;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.heat.HeatingCrucibleTileEntity;
import com.Da_Technomancer.crossroads.crafting.CrucibleRec;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
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

public class HeatingCrucibleCategory implements IRecipeCategory<CrucibleRec>{

	public static final RecipeType<CrucibleRec> TYPE = RecipeType.create(Crossroads.MODID, "heating_crucible", CrucibleRec.class);
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
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CRBlocks.heatingCrucible, 1));
		fluidOverlay = JEICrossroadsPlugin.createFluidOverlay(guiHelper);//guiHelper.createDrawable(new ResourceLocation(Crossroads.MODID, "textures/gui/rectangle_fluid_overlay.png"), 0, 0, 16, 64);
	}

	@Override
	public RecipeType<CrucibleRec> getRecipeType(){
		return TYPE;
	}

	@Override
	public Component getTitle(){
		return CRBlocks.heatingCrucible.getName();
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void draw(CrucibleRec recipe, IRecipeSlotsView view, PoseStack matrix, double mouseX, double mouseY){
		Minecraft.getInstance().font.draw(matrix, Component.translatable("crossroads.jei.crucible.min_temp", HeatingCrucibleTileEntity.TEMP_TIERS[0]), 10, 10, 0x404040);
		Minecraft.getInstance().font.draw(matrix, Component.translatable("crossroads.jei.crucible.required", HeatingCrucibleTileEntity.REQUIRED), 10, 20, 0x404040);
		slot.draw(matrix, 40, 50);
		arrowStatic.draw(matrix, 62, 50);
		arrow.draw(matrix, 62, 50);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CrucibleRec recipe, IFocusGroup focuses){
		builder.addSlot(RecipeIngredientRole.INPUT, 41, 51).addIngredients(recipe.getIngredient());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 31).addIngredient(ForgeTypes.FLUID_STACK, recipe.getOutput()).setFluidRenderer(2000, true, 16, 64).setOverlay(fluidOverlay, 0, 0);
	}

	@Override
	public IDrawable getIcon(){
		return icon;
	}
}

package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.recipes.CopshowiumRec;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
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
import net.minecraftforge.fluids.FluidStack;

public class CopshowiumCategory implements IRecipeCategory<CopshowiumRec>{

	public static final RecipeType<CopshowiumRec> TYPE = RecipeType.create(Crossroads.MODID, "copshowium", CopshowiumRec.class);
	private final IDrawable back;
	private final IDrawable icon;
	private final IDrawableAnimated arrow;
	private final IDrawableStatic arrowStatic;
	private final IDrawable fluidOverlay;

	protected CopshowiumCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(CRBlocks.copshowiumCreationChamber, 1));
		arrowStatic = guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 79, 35, 24, 17);
		arrow = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 176, 14, 24, 17), 40, IDrawableAnimated.StartDirection.LEFT, false);
		fluidOverlay = JEICrossroadsPlugin.createFluidOverlay(guiHelper);
	}

	@Override
	public ResourceLocation getUid(){
		return TYPE.getUid();
	}

	@Override
	public Class<? extends CopshowiumRec> getRecipeClass(){
		return TYPE.getRecipeClass();
	}

	@Override
	public RecipeType<CopshowiumRec> getRecipeType(){
		return TYPE;
	}

	@Override
	public Component getTitle(){
		return CRBlocks.copshowiumCreationChamber.getName();
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void draw(CopshowiumRec rec, IRecipeSlotsView view, PoseStack matrix, double mouseX, double mouseY){
		if(rec.isFlux()){
			Minecraft.getInstance().font.draw(matrix, MiscUtil.localize("crossroads.jei.copshowium.flux"), 10, 10, 4210752);
		}
		arrowStatic.draw(matrix, 75, 56);
		arrow.draw(matrix, 75, 56);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CopshowiumRec recipe, IFocusGroup focuses){
		int displaySize = 1000;
		builder.addSlot(RecipeIngredientRole.INPUT, 51, 31).addIngredients(ForgeTypes.FLUID_STACK, recipe.getInput().getMatchedFluidStacks(displaySize)).setFluidRenderer(4000L, true, 16, 64).setOverlay(fluidOverlay, 0, 0);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 111, 31).addIngredient(ForgeTypes.FLUID_STACK, new FluidStack(CRFluids.moltenCopshowium.still, (int) (displaySize * recipe.getMult()))).setFluidRenderer(4000L, true, 16, 64).setOverlay(fluidOverlay, 0, 0);
	}

	@Override
	public IDrawable getIcon(){
		return icon;
	}
}

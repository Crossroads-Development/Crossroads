package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.recipes.OreCleanserRec;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.tileentities.fluid.OreCleanserTileEntity;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class OreCleanserCategory implements IRecipeCategory<OreCleanserRec>{

	public static final RecipeType<OreCleanserRec> TYPE = RecipeType.create(Crossroads.MODID, "ore_cleanser", OreCleanserRec.class);

	private final IDrawable back;
	private final IDrawable slot;
	private final IDrawableAnimated arrow;
	private final IDrawableStatic arrowStatic;
	private final IDrawable fluidOverlay;
	private final IDrawable icon;
	
	protected OreCleanserCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		slot = guiHelper.getSlotDrawable();
		arrowStatic = guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 79, 35, 24, 17);
		arrow = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 176, 14, 24, 17), 40, IDrawableAnimated.StartDirection.LEFT, false);
		fluidOverlay = JEICrossroadsPlugin.createFluidOverlay(guiHelper);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(CRBlocks.oreCleanser, 1));
	}

	@Override
	public ResourceLocation getUid(){
		return TYPE.getUid();
	}

	@Override
	public Class<? extends OreCleanserRec> getRecipeClass(){
		return TYPE.getRecipeClass();
	}

	@Override
	public RecipeType<OreCleanserRec> getRecipeType(){
		return TYPE;
	}

	@Override
	public Component getTitle(){
		return CRBlocks.oreCleanser.getName();
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
	public void draw(OreCleanserRec recipe, IRecipeSlotsView view, PoseStack matrix, double mouseX, double mouseY){
		slot.draw(matrix, 54, 50);
		slot.draw(matrix, 110, 50);
		arrowStatic.draw(matrix, 78, 50);
		arrow.draw(matrix, 78, 50);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, OreCleanserRec recipe, IFocusGroup focuses){
		builder.addSlot(RecipeIngredientRole.INPUT, 55, 51).addIngredients(recipe.getIngredient());
		builder.addSlot(RecipeIngredientRole.INPUT, 35, 31).addIngredient(ForgeTypes.FLUID_STACK, new FluidStack(CRFluids.steam.still, OreCleanserTileEntity.WATER_USE)).setFluidRenderer(1000L, true, 16, 64).setOverlay(fluidOverlay, 0, 0);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 111, 51).addItemStack(recipe.getResultItem());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 131, 31).addIngredient(ForgeTypes.FLUID_STACK, new FluidStack(CRFluids.dirtyWater.still, OreCleanserTileEntity.WATER_USE)).setFluidRenderer(1000L, true, 16, 64).setOverlay(fluidOverlay, 0, 0);
	}
}

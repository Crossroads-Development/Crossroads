package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.fluids.BlockDirtyWater;
import com.Da_Technomancer.crossroads.fluids.BlockSteam;
import com.Da_Technomancer.crossroads.tileentities.fluid.OreCleanserTileEntity;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class OreCleanserCategory implements IRecipeCategory<OreCleanserRecipe>{

	public static final String ID = Main.MODID + ".ore_cleanser";
	private final IDrawable back;
	private final IDrawable slot;
	private final IDrawableAnimated arrow;
	private final IDrawableStatic arrowStatic;
	private final IDrawable fluidOverlay;

	protected OreCleanserCategory(IGuiHelper guiHelper){
		back = guiHelper.createBlankDrawable(180, 100);
		slot = guiHelper.getSlotDrawable();
		arrowStatic = guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 79, 35, 24, 17);
		arrow = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(new ResourceLocation("textures/gui/container/furnace.png"), 176, 14, 24, 17), 40, StartDirection.LEFT, false);
		fluidOverlay = guiHelper.createDrawable(new ResourceLocation(Main.MODID, "textures/gui/rectangle_fluid_overlay.png"), 0, 0, 16, 64, 16, 64);
	}

	@Override
	public String getUid(){
		return ID;
	}

	@Override
	public String getTitle(){
		return "Ore Cleanser";
	}

	@Override
	public IDrawable getBackground(){
		return back;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft){
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		slot.draw(minecraft, 54, 50);
		slot.draw(minecraft, 110, 50);
		arrowStatic.draw(minecraft, 78, 50);
		arrow.draw(minecraft, 78, 50);
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, OreCleanserRecipe recipeWrapper, IIngredients ingredients){
		if(ingredients.getInputs(ItemStack.class).get(0).isEmpty()){
			// Might happen if CraftTweaker added an invalid recipe
			return;
		}

		recipeLayout.getItemStacks().init(0, true, 54, 50);
		recipeLayout.getItemStacks().set(0, ingredients.getInputs(ItemStack.class).get(0));

		recipeLayout.getItemStacks().init(1, false, 110, 50);
		recipeLayout.getItemStacks().set(1, ingredients.getOutputs(ItemStack.class).get(0));

		recipeLayout.getFluidStacks().init(0, true, 34, 30, 16, 64, 1_000, true, fluidOverlay);
		recipeLayout.getFluidStacks().set(0, new FluidStack(BlockSteam.getSteam(), OreCleanserTileEntity.WATER_USE));

		recipeLayout.getFluidStacks().init(1, false, 130, 30, 16, 64, 1_000, true, fluidOverlay);
		recipeLayout.getFluidStacks().set(1, new FluidStack(BlockDirtyWater.getDirtyWater(), OreCleanserTileEntity.WATER_USE));
	}

	@Override
	public String getModName(){
		return Main.MODNAME;
	}
}

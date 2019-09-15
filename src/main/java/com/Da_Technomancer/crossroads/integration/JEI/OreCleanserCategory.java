package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.fluids.CrossroadsFluids;
import com.Da_Technomancer.crossroads.tileentities.fluid.OreCleanserTileEntity;
import com.google.common.collect.ImmutableList;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class OreCleanserCategory implements IRecipeCategory<OreCleanserRecipe>{

	public static final ResourceLocation ID = new ResourceLocation(Crossroads.MODID, "ore_cleanser");
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
		fluidOverlay = guiHelper.createDrawable(new ResourceLocation(Crossroads.MODID, "textures/gui/rectangle_fluid_overlay.png"), 0, 0, 16, 64);
		icon = guiHelper.createDrawableIngredient(new ItemStack(CrossroadsBlocks.oreCleanser, 1));
	}

	@Override
	public ResourceLocation getUid(){
		return ID;
	}

	@Override
	public Class<? extends OreCleanserRecipe> getRecipeClass(){
		return OreCleanserRecipe.class;
	}

	@Override
	public String getTitle(){
		return CrossroadsBlocks.oreCleanser.getNameTextComponent().getFormattedText();
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
	public void draw(OreCleanserRecipe recipe, double mouseX, double mouseY){
//		GlStateManager.enableAlpha();
//		GlStateManager.enableBlend();
		slot.draw(54, 50);
		slot.draw(110, 50);
		arrowStatic.draw(78, 50);
		arrow.draw(78, 50);
//		GlStateManager.disableBlend();
//		GlStateManager.disableAlpha();
	}

	@Override
	public void setRecipe(IRecipeLayout layout, OreCleanserRecipe recipe, IIngredients ingredients){
		IGuiItemStackGroup itemGroup = layout.getItemStacks();
		IGuiFluidStackGroup fluidGroup = layout.getFluidStacks();

		itemGroup.init(0, true, 54, 50);
		itemGroup.set(0, recipe.inputs);

		itemGroup.init(1, false, 110, 50);
		itemGroup.set(1, recipe.output);

		fluidGroup.init(0, true, 34, 30, 16, 64, 1_000, true, fluidOverlay);
		fluidGroup.set(0, new FluidStack(CrossroadsFluids.steam.still, OreCleanserTileEntity.WATER_USE));

		fluidGroup.init(1, false, 130, 30, 16, 64, 1_000, true, fluidOverlay);
		fluidGroup.set(1, new FluidStack(CrossroadsFluids.dirtyWater.still, OreCleanserTileEntity.WATER_USE));

		itemGroup.set(ingredients);
		fluidGroup.set(ingredients);
	}

	@Override
	public void setIngredients(OreCleanserRecipe recipe, IIngredients ingredients){
		ingredients.setInputLists(VanillaTypes.ITEM, ImmutableList.of(recipe.inputs));
		ingredients.setOutput(VanillaTypes.ITEM, recipe.output);
		ingredients.setInput(VanillaTypes.FLUID, new FluidStack(CrossroadsFluids.steam.still, OreCleanserTileEntity.WATER_USE));
		ingredients.setOutput(VanillaTypes.FLUID, new FluidStack(CrossroadsFluids.dirtyWater.still, OreCleanserTileEntity.WATER_USE));
	}
}

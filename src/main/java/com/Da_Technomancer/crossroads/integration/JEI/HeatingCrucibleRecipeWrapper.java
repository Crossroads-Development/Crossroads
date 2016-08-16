package com.Da_Technomancer.crossroads.integration.JEI;

import java.util.List;

import javax.annotation.Nonnull;

import com.Da_Technomancer.crossroads.fluids.BlockMoltenCopper;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.google.common.collect.ImmutableList;

import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class HeatingCrucibleRecipeWrapper implements IRecipeWrapper{

	private final boolean copper;

	protected HeatingCrucibleRecipeWrapper(@Nonnull HeatingCrucibleRecipe recipe){
		copper = recipe.isCopper();
	}

	@Override
	public List<ItemStack> getInputs(){
		return ImmutableList.of(copper ? new ItemStack(ModItems.dustCopper, 1) : new ItemStack(Blocks.COBBLESTONE, 1));
	}

	@Override
	public List<ItemStack> getOutputs(){
		return ImmutableList.of();
	}

	@Override
	public List<FluidStack> getFluidInputs(){
		return ImmutableList.of();
	}

	@Override
	public List<FluidStack> getFluidOutputs(){
		return ImmutableList.of(new FluidStack(copper ? BlockMoltenCopper.getMoltenCopper() : FluidRegistry.LAVA, 200));
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY){
		minecraft.fontRendererObj.drawString("When above 1000*C", 10, 10, 4210752);
	}

	@Override
	public void drawAnimations(Minecraft minecraft, int recipeWidth, int recipeHeight){

	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY){
		return null;
	}

	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton){
		return false;
	}

}

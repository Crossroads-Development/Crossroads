package com.Da_Technomancer.crossroads.client.integration.JEI;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;

import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import scala.actors.threadpool.Arrays;

public class GrindstoneRecipeWrapper implements IRecipeWrapper{

	private List<ItemStack> inputs;
	private List<ItemStack> outputs;
	
	@SuppressWarnings("unchecked")
	protected GrindstoneRecipeWrapper(@Nonnull GrindstoneRecipe recipe){
		outputs = Arrays.asList(recipe.getStacks());
		if(recipe.getString().contains(":")){
			inputs = ImmutableList.of(new ItemStack(Item.getByNameOrId(recipe.getString()), 1));
		}else{
			inputs = OreDictionary.getOres(recipe.getString());
		}
	}
	
	@Override
	public List<ItemStack> getInputs(){
		return inputs;
	}

	@Override
	public List<ItemStack> getOutputs(){
		return outputs;
	}

	@Override
	public List<FluidStack> getFluidInputs(){
		return ImmutableList.of();
	}

	@Override
	public List<FluidStack> getFluidOutputs(){
		return ImmutableList.of();
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY){
		
	}

	@Override
	public void drawAnimations(Minecraft minecraft, int recipeWidth, int recipeHeight){
		
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY){
		return ImmutableList.of();
	}

	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton){
		return false;
	}

}

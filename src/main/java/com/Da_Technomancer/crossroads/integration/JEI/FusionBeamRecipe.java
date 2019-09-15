package com.Da_Technomancer.crossroads.integration.JEI;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.crafting.RecipePredicate;
import com.google.common.collect.ImmutableList;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class FusionBeamRecipe implements IRecipeWrapper{

	private final Predicate<BlockState> input;
	private final BlockState endState;
	private final int minPower;
	private final boolean voi;

	public FusionBeamRecipe(Predicate<BlockState> input, BlockState endState, int minPower, boolean voi){
		if(!(input instanceof RecipePredicate)){
			Crossroads.logger.warn("Tried to create fusion beam JEI recipe with normal Predicate [" + input.toString() + "]. Report to mod author!");
		}
		this.input = input;
		this.endState = endState;
		this.minPower = minPower;
		this.voi = voi;
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY){
		minecraft.fontRenderer.drawString("Minimum power:  " + minPower, 40, 20, 4210752);
		minecraft.fontRenderer.drawString(voi ? "Void-Fusion beam" : "Fusion beam", 40, 30, 4210752);
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY){
		return null;
	}

	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton){
		return false;
	}

	@Override
	public void getIngredients(IIngredients ingredients){
		ArrayList<ItemStack> ing = new ArrayList<ItemStack>();
		if(input instanceof RecipePredicate){
			for(BlockState state : ((RecipePredicate<BlockState>) input).getMatchingList()){
				ing.add(new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)));
			}
		}
		ingredients.setInput(VanillaTypes.ITEM, ing);
		ingredients.setOutput(VanillaTypes.ITEM, ImmutableList.of(new ItemStack(endState.getBlock(), 1, endState.getBlock().getMetaFromState(endState))));
	}
}

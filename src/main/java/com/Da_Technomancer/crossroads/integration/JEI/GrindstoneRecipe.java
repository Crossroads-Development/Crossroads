package com.Da_Technomancer.crossroads.integration.JEI;

import java.util.Map.Entry;

import com.Da_Technomancer.crossroads.items.crafting.ICraftingStack;

import net.minecraft.item.ItemStack;

public class GrindstoneRecipe{

	private final ICraftingStack input;
	private final ItemStack[] stacks;

	public GrindstoneRecipe(Entry<ICraftingStack, ItemStack[]> in){
		input = in.getKey();
		stacks = in.getValue();
	}

	public ICraftingStack getInput(){
		return input;
	}

	public ItemStack[] getStacks(){
		return stacks;
	}

}

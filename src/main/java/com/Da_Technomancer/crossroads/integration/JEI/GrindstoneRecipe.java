package com.Da_Technomancer.crossroads.integration.JEI;

import java.util.Map.Entry;

import net.minecraft.item.ItemStack;

public class GrindstoneRecipe{
	
	private final String string;
	private final ItemStack[] stacks;
	
	public GrindstoneRecipe(Entry<String, ItemStack[]> in){
		string = in.getKey();
		stacks = in.getValue();
	}
	
	public String getString(){
		return string;
	}
	
	public ItemStack[] getStacks(){
		return stacks;
	}

}

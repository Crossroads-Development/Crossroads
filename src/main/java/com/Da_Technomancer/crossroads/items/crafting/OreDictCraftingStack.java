package com.Da_Technomancer.crossroads.items.crafting;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictCraftingStack implements ICraftingStack{

	private final String oreDict;
	private final int count;

	public OreDictCraftingStack(String oreDict, int count){
		this.oreDict = oreDict;
		this.count = count;
	}

	@Override
	public boolean match(ItemStack stack){
		if(stack == null || count != stack.stackSize){
			return false;
		}

		for(int ID : OreDictionary.getOreIDs(stack)){
			if(OreDictionary.getOreName(ID) == oreDict){
				return true;
			}
		}

		return false;
	}

	/**
	 * Same as match, but ignores item count
	 */
	@Override
	public boolean softMatch(ItemStack stack){
		if(stack == null){
			return false;
		}

		for(int ID : OreDictionary.getOreIDs(stack)){
			if(OreDictionary.getOreName(ID).equals(oreDict)){
				return true;
			}
		}

		return false;
	}

	@Override
	public List<ItemStack> getMatchingList(){
		return OreDictionary.getOres(oreDict, false);
	}
	
	protected String getOreDict(){
		return oreDict;
	}
	
	protected int getCount(){
		return count;
	}

	@Override
	public boolean equals(Object other){
		if(this == other){
			return true;
		}
		if(other instanceof OreDictCraftingStack){
			OreDictCraftingStack otherStack = (OreDictCraftingStack) other;
			return oreDict.equals(otherStack.getOreDict()) && count == otherStack.getCount();
		}
		
		return false;
	}
}

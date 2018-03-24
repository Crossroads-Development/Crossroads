package com.Da_Technomancer.crossroads.items.crafting;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictCraftingStack implements RecipePredicate<ItemStack>{

	private final String oreDict;

	public OreDictCraftingStack(String oreDict){
		this.oreDict = oreDict;
	}

	@Override
	public boolean test(ItemStack stack){
		if(stack.isEmpty()){
			return false;
		}

		for(int ID : OreDictionary.getOreIDs(stack)){
			if(OreDictionary.getOreName(ID) == oreDict){
				return true;
			}
		}

		return false;
	}

	@Override
	public List<ItemStack> getMatchingList(){
		return OreDictionary.getOres(oreDict, false);
	}

	@Override
	public boolean equals(Object other){
		if(this == other){
			return true;
		}
		if(other instanceof OreDictCraftingStack){
			OreDictCraftingStack otherStack = (OreDictCraftingStack) other;
			return oreDict.equals(otherStack.oreDict);
		}
		
		return false;
	}
	
	@Override
	public String toString(){
		return "OreDictCraftingStack[OreDict: " + oreDict + "]";
	}
	
	@Override
	public int hashCode(){
		return oreDict.hashCode();
	}
}

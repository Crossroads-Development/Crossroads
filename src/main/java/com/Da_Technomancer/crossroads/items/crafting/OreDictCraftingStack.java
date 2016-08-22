package com.Da_Technomancer.crossroads.items.crafting;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictCraftingStack extends CraftingStack{

	private final String oreDict;
	private final int count;

	public OreDictCraftingStack(String oreDict, int count){
		super((Item) null, 1, 0);
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

}

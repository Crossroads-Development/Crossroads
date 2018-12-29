package com.Da_Technomancer.crossroads.items.crafting;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class ComponentCraftingStack implements RecipePredicate<ItemStack>{

	private final String prefix;

	public ComponentCraftingStack(String prefix){
		this.prefix = prefix;
	}

	@Override
	public boolean test(ItemStack stack){
		for(String metal : OreSetup.metalStages.keySet()){
			if(MiscUtil.hasOreDict(stack, prefix + metal)){
				return true;
			}
		}

		return false;
	}

	@Override
	public List<ItemStack> getMatchingList(){
		ArrayList<ItemStack> matches = new ArrayList<>();
		for(String metal : OreSetup.metalStages.keySet()){
			matches.addAll(OreDictionary.getOres(prefix + metal, false));
		}
		return matches;
	}

	@Override
	public boolean equals(Object other){
		if(this == other){
			return true;
		}
		if(other instanceof ComponentCraftingStack){
			ComponentCraftingStack otherStack = (ComponentCraftingStack) other;
			return prefix.equals(otherStack.prefix);
		}
		
		return false;
	}
	
	@Override
	public String toString(){
		return "ComponentCraftingStack[Prefix: " + prefix + "]";
	}
	
	@Override
	public int hashCode(){
		return prefix.hashCode();
	}
}

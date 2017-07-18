package com.Da_Technomancer.crossroads.items.crafting;

import java.util.List;

import net.minecraft.item.ItemStack;

/**
 * The purpose of this interface is to allow for recipe inputs with arbitrary requirements,
 * instead of just the usual OreDict/Item & count.
 * 
 * Implementers of this interface should override {@link Object#equals(Object)}
 */
public interface ICraftingStack{
	
	/**
	 * Whether the ItemStack matches the Crafting Stack's Criteria.
	 * 
	 */
	public boolean match(ItemStack stack);
	
	/**
	 * Same as match, but ignores item count
	 * 
	 */
	public boolean softMatch(ItemStack stack);
	
	/**
	 * A list of every ItemStack that matches this stack, it need not be exhaustive (every possible metadata, NBT, stacksize, etc.)
	 * This is mostly used for JEI support.
	 */
	public List<ItemStack> getMatchingList();
}

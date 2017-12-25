package com.Da_Technomancer.crossroads.items.crafting;

import java.util.List;

/**
 * The purpose of this interface is to allow for recipe inputs with arbitrary requirements,
 * instead of just the usual OreDict/Item & count.
 * 
 * T defines what this class matches over (ex. ItemStack or IBlockState)
 * 
 * Implementers of this interface should override {@link Object#equals(Object)}
 */
public interface ICraftingStack<T>{
	
	/**
	 * Whether the ItemStack matches the Crafting Stack's Criteria.
	 * 
	 */
	public boolean match(T toCheck);
	
	/**
	 * Same as match, but ignores quantity
	 * 
	 */
	public boolean softMatch(T toCheck);
	
	/**
	 * A list of every T that matches this stack, it need not be exhaustive (every possible metadata, NBT, stacksize, etc.)
	 * This is mostly used for JEI support.
	 */
	public List<T> getMatchingList();
}

package com.Da_Technomancer.crossroads.items.crafting;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 
 * Implementers of this interface should override .equals
 *
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
	@SideOnly(Side.CLIENT)
	public List<ItemStack> getMatchingList();
}

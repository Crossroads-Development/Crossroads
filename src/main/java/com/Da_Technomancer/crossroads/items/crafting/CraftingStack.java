package com.Da_Technomancer.crossroads.items.crafting;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CraftingStack{

	private final Item item;
	private final int count;
	private final int meta;

	public CraftingStack(Block block, int count, int meta){
		this(Item.getItemFromBlock(block), count, meta);
	}

	public CraftingStack(Item item, int count, int meta){
		this.item = item;
		this.count = count;
		this.meta = meta;
	}

	public boolean match(ItemStack stack){
		if(stack == null){
			return false;
		}

		if(stack.getItem() == item && stack.stackSize == count && stack.getMetadata() == meta){
			return true;
		}

		return false;
	}

	/**
	 * Same as match, but ignores item count
	 * 
	 */
	public boolean softMatch(ItemStack stack){
		if(stack == null){
			return false;
		}

		if(stack.getItem() == item && stack.getMetadata() == meta){
			return true;
		}

		return false;
	}

}

package com.Da_Technomancer.crossroads.items.crafting;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class CraftingStack implements ICraftingStack{

	private final Item item;
	private final int count;
	private final int meta;

	/**
	 * 
	 * @param block
	 * @param count
	 * @param meta A value of -1 means to ignore metadata
	 */
	public CraftingStack(Block block, int count, int meta){
		this(Item.getItemFromBlock(block), count, meta);
	}

	/**
	 * 
	 * @param item
	 * @param count
	 * @param meta A value of -1 means to ignore metadata
	 */
	public CraftingStack(Item item, int count, int meta){
		this.item = item;
		this.count = count;
		this.meta = meta;
	}

	@Override
	public boolean match(ItemStack stack){
		if(stack.isEmpty()){
			return false;
		}

		if(stack.getItem() == item && stack.getCount() == count && (meta == -1 || stack.getMetadata() == meta)){
			return true;
		}

		return false;
	}

	@Override
	public boolean softMatch(ItemStack stack){
		if(stack.isEmpty()){
			return false;
		}

		if(stack.getItem() == item && (meta == -1 || stack.getMetadata() == meta)){
			return true;
		}

		return false;
	}

	@Override
	public List<ItemStack> getMatchingList(){
		if(meta != -1 || !item.getHasSubtypes()){
			return ImmutableList.of(new ItemStack(item, count, meta));
		}
		NonNullList<ItemStack> list = NonNullList.create();
		item.getSubItems(CreativeTabs.SEARCH, list);
		return list;
	}
	
	@Override
	public boolean equals(Object other){
		if(other == this){
			return true;
		}
		if(other instanceof CraftingStack){
			CraftingStack otherStack = (CraftingStack) other;
			return item == otherStack.item && meta == otherStack.meta && count == otherStack.count;
		}
		
		return false;
	}
}

package com.Da_Technomancer.crossroads.items.crafting;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public class ItemRecipePredicate implements RecipePredicate<ItemStack>{

	private final Item item;
	private final int meta;

	/**
	 * 
	 * @param block
	 * @param meta A value of -1 means to ignore metadata
	 */
	public ItemRecipePredicate(Block block, int meta){
		this(Item.getItemFromBlock(block), meta);
	}

	/**
	 * 
	 * @param item
	 * @param meta A value of -1 or OreDictionary.WILDCARD_VALUE means to ignore metadata
	 */
	public ItemRecipePredicate(Item item, int meta){
		this.item = item;
		this.meta = meta == -1 ? OreDictionary.WILDCARD_VALUE : meta;
	}

	@Override
	public boolean test(ItemStack stack){
		if(stack.isEmpty()){
			return false;
		}

		return stack.getItem() == item && (meta == OreDictionary.WILDCARD_VALUE || stack.getMetadata() == meta);

	}

	@Override
	public List<ItemStack> getMatchingList(){
		if(meta != -1 || !item.getHasSubtypes()){
			return ImmutableList.of(new ItemStack(item, 1, meta));
		}
		NonNullList<ItemStack> list = NonNullList.create();
		item.getSubItems(ItemGroup.SEARCH, list);
		return list;
	}
	
	@Override
	public boolean equals(Object other){
		if(other == this){
			return true;
		}
		if(other instanceof ItemRecipePredicate){
			ItemRecipePredicate otherStack = (ItemRecipePredicate) other;
			return item == otherStack.item && meta == otherStack.meta;
		}
		
		return false;
	}
	
	@Override
	public String toString(){
		return "CraftingStack[Item: " + item.getRegistryName() + ", Meta: " + meta + "]";
	}
	
	@Override
	public int hashCode(){
		return (item.hashCode() << 1) + (meta & 1);
	}
}

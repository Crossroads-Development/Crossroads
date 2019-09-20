package com.Da_Technomancer.crossroads.items.crafting;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class TagCraftingStack implements RecipePredicate<ItemStack>{

	private final Tag<Item> tag;

	public TagCraftingStack(ResourceLocation tag){
		this(new ItemTags.Wrapper(tag));
	}

	public TagCraftingStack(Tag<Item> tag){
		this.tag = tag;
	}

	@Override
	public boolean test(ItemStack stack){
		return tag.contains(stack.getItem());
	}

	@Override
	public List<ItemStack> getMatchingList(){
		ArrayList<ItemStack> toReturn = new ArrayList<>(tag.getAllElements().size());
		tag.getAllElements().stream().map((item -> new ItemStack(item, 1))).forEach(toReturn::add);
		return toReturn;
	}

	@Override
	public boolean equals(Object other){
		if(this == other){
			return true;
		}
		if(other instanceof TagCraftingStack){
			TagCraftingStack otherStack = (TagCraftingStack) other;
			return tag.getId().equals(otherStack.tag.getId());
		}
		
		return false;
	}
	
	@Override
	public String toString(){
		return "TagCraftingStack[OreDict: " + tag.getId().toString() + "]";
	}
	
	@Override
	public int hashCode(){
		return tag.getId().hashCode();
	}
}

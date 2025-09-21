package com.Da_Technomancer.crossroads.api.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public record FixedRecipeInput(@Nullable BlockEntity te, ItemStack... items) implements RecipeInput{

	@Override
	public ItemStack getItem(int i){
		return i < 0 || i >= items.length ? ItemStack.EMPTY : items[i];
	}

	@Override
	public int size(){
		return items.length;
	}

	@Override
	public boolean isEmpty(){
		return RecipeInput.super.isEmpty();
	}
}

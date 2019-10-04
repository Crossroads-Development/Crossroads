package com.Da_Technomancer.crossroads.items.crafting.recipes;

import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SingleItemRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class StampMillRec extends SingleItemRecipe{

	public StampMillRec(ResourceLocation location, String name, Ingredient input, ItemStack output){
		super(RecipeHolder.STAMP_MILL_TYPE, RecipeHolder.STAMP_MILL_SERIAL, location, name, input, output);
	}

	@Override
	public boolean matches(IInventory inv, World worldIn){
		return ingredient.test(inv.getStackInSlot(0));
	}

	@Override
	public ItemStack getIcon(){
		return new ItemStack(CrossroadsBlocks.stampMill);
	}
}

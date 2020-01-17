package com.Da_Technomancer.crossroads.items.crafting.recipes;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SingleItemRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class OreCleanserRec extends SingleItemRecipe{

	public OreCleanserRec(ResourceLocation location, String name, Ingredient input, ItemStack output){
		super(RecipeHolder.ORE_CLEANSER_TYPE, RecipeHolder.ORE_CLEANSER_SERIAL, location, name, input, output);
	}

	@Override
	public boolean matches(IInventory inv, World worldIn){
		return ingredient.test(inv.getStackInSlot(0));
	}

	@Override
	public ItemStack getIcon(){
		return new ItemStack(CRBlocks.oreCleanser);
	}
}

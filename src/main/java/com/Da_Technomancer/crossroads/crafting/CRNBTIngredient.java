package com.Da_Technomancer.crossroads.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.NBTIngredient;

/**
 * Literally just an extension of NBTIngredient that's public so we can get at the constructor
 * Note that it will deserialize to an NBTIngredient, which should behave identically
 */
public class CRNBTIngredient extends NBTIngredient{

	public CRNBTIngredient(ItemStack stack){
		super(stack);
	}
}

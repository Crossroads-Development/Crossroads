package com.Da_Technomancer.crossroads.api.templates;

import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/**
 * If placed on an item, will use the results of populateCreativeTab() to add items to the creative tab when registered, rather than the default
 */
public interface ICreativeTabPopulatingItem extends Supplier<ItemStack[]>{

	/**
	 * @return Itemstacks to add to the creative tab. Array can be empty
	 */
	ItemStack[] populateCreativeTab();

	@Override
	default ItemStack[] get(){
		return populateCreativeTab();
	}
}

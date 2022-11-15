package com.Da_Technomancer.crossroads.integration.curios;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;
import java.util.function.Predicate;

public class VanillaInventoryProxy implements IInventoryProxy{

	/**
	 * Finds an item on a player. Checks the mainhand and offhand first, then curios slots
	 * Safe to call when Curios is not installed
	 * @param itemFilter A predicate matching the item types to search for
	 * @param player The player for search
	 * @return The itemstack in the player inventory containing the item. It is mutable, and will write back. Returns ItemStack.EMPTY if not found
	 */
	@Override
	public ItemStack getEquipped(Predicate<ItemStack> itemFilter, LivingEntity player){
		//Check mainhand
		ItemStack held = player.getMainHandItem();
		if(itemFilter.test(held)){
			return held;
		}
		//Check offhand
		held = player.getOffhandItem();
		if(itemFilter.test(held)){
			return held;
		}

		return ItemStack.EMPTY;
	}

	/**
	 * Apply a function for all items in a Player's inventory, including curios.
	 * Curios are applied before the rest of the inventory
	 * @param player The player
	 * @param stackModifier A function to modify ItemStacks. DO NOT modify the incoming stack directly, only return the target variant
	 */
	@Override
	public void forAllInventoryItems(Player player, Function<ItemStack, ItemStack> stackModifier){
		Inventory inv = player.getInventory();
		for(int i = 0; i < inv.getContainerSize(); i++){
			ItemStack srcStack = inv.getItem(i);
			ItemStack resStack = stackModifier.apply(srcStack);
			if(!srcStack.equals(resStack, false)){
				inv.setItem(i, resStack);
			}
		}
	}
}

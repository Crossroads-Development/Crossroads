package com.Da_Technomancer.crossroads.integration.curios;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;
import java.util.function.Predicate;

public interface IInventoryProxy{

	/**
	 * Finds an item on a player. Checks the mainhand and offhand first, then curios slots
	 * Safe to call when Curios is not installed
	 * @param item The item to search for
	 * @param player The player for search
	 * @return The itemstack in the player inventory containing the item. It is mutable, and will write back. Returns ItemStack.EMPTY if not found
	 */
	default ItemStack getEquipped(Item item, LivingEntity player){
		return getEquipped(stack -> stack.getItem() == item, player);
	}

	/**
	 * Finds an item on a player. Checks the mainhand and offhand first, then curios slots
	 * @param itemFilter A predicate matching the item types to search for
	 * @param player The player for search
	 * @return The itemstack in the player inventory containing the item. It is mutable, and will write back. Returns ItemStack.EMPTY if not found
	 */
	ItemStack getEquipped(Predicate<ItemStack> itemFilter, LivingEntity player);

	/**
	 * Apply a function for all items in a Player's inventory, including curios.
	 * Curios are applied before the rest of the inventory
	 * @param player The player
	 * @param stackModifier A function to modify ItemStacks. DO NOT modify the incoming stack directly, only return the target variant
	 */
	void forAllInventoryItems(Player player, Function<ItemStack, ItemStack> stackModifier);
}

package com.Da_Technomancer.crossroads.integration.curios;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Interacts with the Curios mod
 * This class is safe to interact with even when Curios is not installed. All interaction with Curios should be done through the class.
 * Non-safe code that requires Curios is done in CurioCRCore.java, which should only be accessed through this class
 */
public class CurioHelper{

	protected static final String CURIOS_ID = "curios";
	private static IInventoryProxy inventoryProxy;

	/**
	 * Initializes integration with Curios if installed, and tracks whether to use code in CurioCRCore
	 */
	public static void initIntegration(){
		if(ModList.get().isLoaded(CURIOS_ID)){
			// reflection to avoid hard dependency
			try{
				inventoryProxy = Class.forName("com.Da_Technomancer.crossroads.integration.curios.CuriosInventoryProxy").asSubclass(IInventoryProxy.class).getDeclaredConstructor().newInstance();
			}catch(Exception e){
				inventoryProxy = new VanillaInventoryProxy();
				Crossroads.logger.error("Error while initiating Curios integration; report to mod author", e);
			}
			//Register the proxy so it can request slots
			FMLJavaModLoadingContext.get().getModEventBus().register(inventoryProxy);
		}else{
			inventoryProxy = new VanillaInventoryProxy();
		}
	}

	/**
	 * Finds an item on a player. Checks the mainhand and offhand first, then curios slots
	 * Safe to call when Curios is not installed
	 * @param item The item to search for
	 * @param player The player for search
	 * @return The itemstack in the player inventory containing the item. It is mutable, and will write back. Returns ItemStack.EMPTY if not found
	 */
	public static ItemStack getEquipped(Item item, LivingEntity player){
		return inventoryProxy.getEquipped(item, player);
	}

	/**
	 * Finds an item on a player. Checks the mainhand and offhand first, then curios slots
	 * Safe to call when Curios is not installed
	 * @param itemFilter A predicate matching the item types to search for
	 * @param player The player for search
	 * @return The itemstack in the player inventory containing the item. It is mutable, and will write back. Returns ItemStack.EMPTY if not found
	 */
	public static ItemStack getEquipped(Predicate<ItemStack> itemFilter, LivingEntity player){
		return inventoryProxy.getEquipped(itemFilter, player);
	}

	/**
	 * Apply a function for all items in a Player's inventory, including curios.
	 * Curios are applied before the rest of the inventory
	 * @param player The player
	 * @param stackModifier A function to modify ItemStacks. DO NOT modify the incoming stack directly, only return the target variant
	 */
	public static void forAllInventoryItems(Player player, Function<ItemStack, ItemStack> stackModifier){
		inventoryProxy.forAllInventoryItems(player, stackModifier);
	}
}

package com.Da_Technomancer.crossroads.integration.curios;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Interacts with the Curios mod
 * This class is safe to interact with even when Curios is not installed. All interaction with Curios should be done through the class.
 * Non-safe code that requires Curios is done in CurioCRCore.java, which should only be accessed through this class
 */
public class CurioHelperSafe{

	protected static final String CURIOS_ID = "curios";
	private static boolean foundCurios;

	/**
	 * Initializes integration with Curios if installed, and tracks whether to use code in CurioCRCore
	 */
	public static void initIntegration(){
		foundCurios = ModList.get().isLoaded(CURIOS_ID);
		if(foundCurios){
			FMLJavaModLoadingContext.get().getModEventBus().register(CurioCoreUnsafe.class);
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
		return getEquipped(stack -> stack.getItem() == item, player);
	}

	/**
	 * Finds an item on a player. Checks the mainhand and offhand first, then curios slots
	 * Safe to call when Curios is not installed
	 * @param itemFilter A predicate matching the item types to search for
	 * @param player The player for search
	 * @return The itemstack in the player inventory containing the item. It is mutable, and will write back. Returns ItemStack.EMPTY if not found
	 */
	public static ItemStack getEquipped(Predicate<ItemStack> itemFilter, LivingEntity player){
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

		//Check curios, if applicable
		if(foundCurios){
			Optional<SlotResult> result = CurioCoreUnsafe.findFirstCurio(itemFilter, player);
			if(result.isPresent()){
				return result.get().stack();
			}
		}

		return ItemStack.EMPTY;
	}

	/**
	 * Apply a function for all items in a Player's inventory, including curios.
	 * Curios are applied before the rest of the inventory
	 * @param player The player
	 * @param stackModifier A function to modify ItemStacks. DO NOT modify the incoming stack directly, only return the target variant
	 */
	public static void forAllInventoryItems(Player player, Function<ItemStack, ItemStack> stackModifier){
		LazyOptional<IItemHandlerModifiable> curioOpt = CurioCoreUnsafe.getAllCurios(player);
		if(curioOpt.isPresent()){
			IItemHandlerModifiable curioCont = curioOpt.orElseThrow(NullPointerException::new);
			for(int i = 0; i < curioCont.getSlots(); i++){
				ItemStack srcStack = curioCont.getStackInSlot(i);
				ItemStack resStack = stackModifier.apply(srcStack);
				if(!srcStack.equals(resStack, false)){
					curioCont.setStackInSlot(i, resStack);
				}
			}
		}

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

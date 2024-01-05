package com.Da_Technomancer.crossroads.integration.curios;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class CuriosInventoryProxy implements IInventoryProxy{

	protected static IInventoryProxy create(){
		return new CuriosInventoryProxy();
	}

//	@SubscribeEvent
//	@SuppressWarnings("unused")
//	public void requestSlots(InterModEnqueueEvent evt){
//		//This class is put on the FML bus in CurioHelper.java
//		//We request a charm slot
//		InterModComms.sendTo(CurioHelper.CURIOS_ID, SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("charm").build());
//	}

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

		//Check curios, if applicable
		LazyOptional<ICuriosItemHandler> curioOpt = CuriosApi.getCuriosInventory(player);
		if(curioOpt.isPresent()){
			Optional<SlotResult> resultOpt = curioOpt.orElseThrow(NullPointerException::new).findFirstCurio(itemFilter);
			if(resultOpt.isPresent()){
				return resultOpt.get().stack();
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
	@Override
	public void forAllInventoryItems(Player player, Function<ItemStack, ItemStack> stackModifier){

		LazyOptional<ICuriosItemHandler> curioOpt = CuriosApi.getCuriosInventory(player);
		if(curioOpt.isPresent()){
			IItemHandlerModifiable curioCont = curioOpt.orElseThrow(NullPointerException::new).getEquippedCurios();
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

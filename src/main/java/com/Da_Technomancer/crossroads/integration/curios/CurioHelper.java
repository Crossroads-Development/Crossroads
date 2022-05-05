package com.Da_Technomancer.crossroads.integration.curios;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

/**
 * Interacts with the Curios mod
 * This class is safe to interact with even when Curios is not installed. All interaction with Curios should be done through the class.
 * Non-safe code that requires Curios is done in CurioCRCore.java, which should only be accessed through this class
 */
public class CurioHelper{

	protected static final String CURIOS_ID = "curios";
	private static boolean foundCurios;

	/**
	 * Initializes integration with Curios if installed, and tracks whether to use code in CurioCRCore
	 */
	public static void initIntegration(){
		foundCurios = ModList.get().isLoaded(CURIOS_ID);
		if(foundCurios){
			FMLJavaModLoadingContext.get().getModEventBus().register(CurioCRCore.class);
		}
	}

	/**
	 * Finds an item on a player. Checks the offhand first, then curios slots
	 * Safe to call when Curios is not installed
	 * @param item The item to search for
	 * @param player The player for search
	 * @return The itemstack in the player inventory containing the item. It is mutable, and will write back. Returns ItemStack.EMPTY if not found
	 */
	public static ItemStack getEquipped(Item item, LivingEntity player){
		//Check offhand
		ItemStack held = player.getOffhandItem();
		if(held.getItem() == item){
			return held;
		}

		//Check curios, if applicable
		if(foundCurios){
			Optional<SlotResult> result = CurioCRCore.findFirstCurio(item, player);
			if(result.isPresent()){
				return result.get().stack();
			}
		}

		return ItemStack.EMPTY;
	}
}

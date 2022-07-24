package com.Da_Technomancer.crossroads.integration.curios;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.SlotTypeMessage;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Interaction with the Curios mod
 * This class requires the Curios mod to be loaded to not crash
 * DO NOT interact directly- go through CurioHelper instead
 */
final class CurioCoreUnsafe{

	@SubscribeEvent
	@SuppressWarnings("unused")
	public static void requestSlots(InterModEnqueueEvent evt){
		//This class is put on the FML bus in CurioHelper.java
		//We request a charm slot
		InterModComms.sendTo(CurioHelperSafe.CURIOS_ID, SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("charm").build());
	}

	//Just calls the method in CuriosAPI.
	//This exists to add an additional layer between the class which may not exist at runtime, and Crossroads
	static Optional<SlotResult> findFirstCurio(Item item, @Nonnull LivingEntity livingEntity){
		return CuriosApi.getCuriosHelper().findFirstCurio(livingEntity, item);
	}
}

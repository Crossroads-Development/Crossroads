package com.Da_Technomancer.crossroads.integration.curios;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.imc.CurioIMCMessage;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Interaction with the Curios mod
 * This class requires the Curios mod to be loaded to not crash
 * DO NOT interact directly- go through CurioHelper instead
 */
public final class CurioCRCore{

	@SubscribeEvent
	@SuppressWarnings("unused")
	public static void requestSlots(InterModEnqueueEvent evt){
		//This class is put on the FML bus in CurioHelper.java
		//We request a charm slot
		InterModComms.sendTo(CurioHelper.CURIOS_ID, CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("charm"));
	}

	//Just calls the method in CuriosAPI.
	//This exists to add an additional layer between the class which may not exist at runtime, and Crossroads
	protected static Optional<ImmutableTriple<String, Integer, ItemStack>> getEquippedCurio(Item item, @Nonnull LivingEntity livingEntity){
		return CuriosAPI.getCurioEquipped(item, livingEntity);
	}
}

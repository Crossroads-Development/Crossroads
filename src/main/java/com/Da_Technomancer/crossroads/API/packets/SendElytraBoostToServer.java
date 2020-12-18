package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.technomancy.ArmorPropellerPack;
import com.Da_Technomancer.essentials.packets.ServerPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class SendElytraBoostToServer extends ServerPacket{

	public SendElytraBoostToServer(){

	}

	@Nonnull
	@Override
	protected Field[] getFields(){
		return new Field[0];
	}

	@Override
	protected void run(@Nullable ServerPlayerEntity sender){
		if(sender != null){
			ItemStack chestplate = sender.getItemStackFromSlot(EquipmentSlotType.CHEST);
			if(sender.isElytraFlying() && chestplate.getItem() == CRItems.propellerPack && CRItems.propellerPack.getWindLevel(chestplate) > 0){
				CRItems.propellerPack.setWindLevel(chestplate, CRItems.propellerPack.getWindLevel(chestplate) - ArmorPropellerPack.WIND_PER_BOOST);
				ArmorPropellerPack.applyMidairBoost(sender);
			}
		}
	}
}

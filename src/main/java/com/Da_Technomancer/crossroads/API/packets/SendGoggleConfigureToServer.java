package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.essentials.packets.ServerPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class SendGoggleConfigureToServer extends ServerPacket{

	public String lensName;
	public boolean newSetting;

	private static final Field[] FIELDS = fetchFields(SendGoggleConfigureToServer.class, "lensName", "newSetting");

	public SendGoggleConfigureToServer(){

	}

	public SendGoggleConfigureToServer(EnumGoggleLenses lens, boolean setting){
		this.lensName = lens.name();
		this.newSetting = setting;
	}

	@Nonnull
	@Override
	protected Field[] getFields(){
		return FIELDS;
	}

	@Override
	protected void run(@Nullable ServerPlayerEntity player){
		if(player != null){
			ItemStack stack = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
			if(stack.getItem() == CRItems.moduleGoggles && stack.hasTag() && stack.getTag().contains(lensName)){
				stack.getTag().putBoolean(lensName, newSetting);

				if(EnumGoggleLenses.DIAMOND.name().equals(lensName)){
					StoreNBTToClient.syncNBTToClient(player);
					//TODO player.openGui(Crossroads.instance, GuiHandler.FAKE_CRAFTER_GUI, player.world, (int) player.posX, (int) player.posY, (int) player.posZ);
				}
			}
		}
	}
}

package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.crossroads.items.technomancy.BeamUsingItem;
import com.Da_Technomancer.essentials.api.packets.ServerPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class SendBeamItemToServer extends ServerPacket{

	public byte[] newSetting;

	private static final Field[] FIELDS = fetchFields(SendBeamItemToServer.class, "newSetting");

	@SuppressWarnings("unused")
	public SendBeamItemToServer(){

	}

	public SendBeamItemToServer(byte[] newSetting){
		this.newSetting = newSetting;
	}

	@Nonnull
	@Override
	protected Field[] getFields(){
		return FIELDS;
	}

	@Override
	protected void run(@Nullable ServerPlayer player){
		if(player != null){
			ItemStack stack;
			stack = player.getMainHandItem();
			if(!(stack.getItem() instanceof BeamUsingItem)){
				stack = player.getOffhandItem();
			}
			if(stack.getItem() instanceof BeamUsingItem){
				BeamUsingItem.setSetting(stack, newSetting);
			}
		}
	}
}

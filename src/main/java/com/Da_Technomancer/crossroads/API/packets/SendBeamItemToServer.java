package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.items.technomancy.BeamUsingItem;
import com.Da_Technomancer.essentials.packets.ServerPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;

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
	protected void run(@Nullable ServerPlayerEntity player){
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

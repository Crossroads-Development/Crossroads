package com.Da_Technomancer.crossroads.API.packets;

import net.minecraft.entity.player.ServerPlayerEntity;

import javax.annotation.Nullable;

public interface IDoubleReceiver{

	public void receiveDouble(byte context, double message, @Nullable ServerPlayerEntity sendingPlayer);

}

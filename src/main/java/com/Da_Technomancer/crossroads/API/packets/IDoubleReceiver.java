package com.Da_Technomancer.crossroads.API.packets;

import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public interface IDoubleReceiver{

	public void receiveDouble(byte context, double message, @Nullable ServerPlayer sendingPlayer);

}

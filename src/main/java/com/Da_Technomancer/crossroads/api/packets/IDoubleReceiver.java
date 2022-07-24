package com.Da_Technomancer.crossroads.api.packets;

import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public interface IDoubleReceiver{

	void receiveDouble(byte context, double message, @Nullable ServerPlayer sendingPlayer);

}

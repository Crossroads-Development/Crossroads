package com.Da_Technomancer.crossroads.API.packets;

import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public interface IIntArrayReceiver{

	void receiveInts(byte context, int[] message, @Nullable ServerPlayer sendingPlayer);

}

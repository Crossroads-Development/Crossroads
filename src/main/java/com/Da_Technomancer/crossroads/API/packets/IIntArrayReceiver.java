package com.Da_Technomancer.crossroads.API.packets;

import net.minecraft.entity.player.ServerPlayerEntity;

import javax.annotation.Nullable;

public interface IIntArrayReceiver{

	void receiveInts(byte context, int[] message, @Nullable ServerPlayerEntity sendingPlayer);

}

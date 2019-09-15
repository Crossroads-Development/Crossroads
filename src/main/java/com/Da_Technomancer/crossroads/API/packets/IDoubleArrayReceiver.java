package com.Da_Technomancer.crossroads.API.packets;

import javax.annotation.Nullable;

import net.minecraft.entity.player.ServerPlayerEntity;

public interface IDoubleArrayReceiver{

	public void receiveDoubles(String context, double message[], @Nullable ServerPlayerEntity sendingPlayer);

}

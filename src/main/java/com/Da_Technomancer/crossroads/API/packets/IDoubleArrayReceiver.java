package com.Da_Technomancer.crossroads.API.packets;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayerMP;

public interface IDoubleArrayReceiver{

	public void receiveDoubles(String context, double message[], @Nullable EntityPlayerMP sendingPlayer);

}

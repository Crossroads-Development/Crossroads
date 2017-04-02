package com.Da_Technomancer.crossroads.API.packets;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayerMP;

public interface IIntReceiver{

	/**
	 * @param context What the message represents.
	 * @param message The actual value being sent. 
	 * @param sendingPlayer The player who sent the packet. WILL BE NULL IF RECEIVED ON CLIENT SIDE. May also be null if received on server side. 
	 */
	public void receiveInt(String context, int message, @Nullable EntityPlayerMP sendingPlayer);

}

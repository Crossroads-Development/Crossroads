package com.Da_Technomancer.crossroads.API.packets;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayerMP;

public interface IIntReceiver{

	/**
	 * @param identifier Allows distinction between message purpose. 
	 * @param message The actual value being sent. 
	 * @param sendingPlayer The player who sent the packet. WILL BE NULL IF RECEIVED ON CLIENT SIDE. May also be null if received on server side. 
	 */
	public void receiveInt(byte identifier, int message, @Nullable EntityPlayerMP sendingPlayer);

}

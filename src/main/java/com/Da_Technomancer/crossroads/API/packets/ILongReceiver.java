package com.Da_Technomancer.crossroads.API.packets;

import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nullable;

public interface ILongReceiver{

	/**
	 * @param identifier Allows distinction between message purpose. 
	 * @param message The actual value being sent. 
	 * @param sendingPlayer The player who sent the packet. WILL BE NULL IF RECEIVED ON CLIENT SIDE. May also be null if received on server side. 
	 */
	public void receiveLong(byte identifier, long message, @Nullable EntityPlayerMP sendingPlayer);

}

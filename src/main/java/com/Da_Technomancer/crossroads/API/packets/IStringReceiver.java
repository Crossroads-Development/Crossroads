package com.Da_Technomancer.crossroads.API.packets;

import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public interface IStringReceiver{

	/**
	 * @param context What the message represents.
	 * @param message The actual value being sent. 
	 * @param sender The player who sent the packet. WILL BE NULL IF RECEIVED ON CLIENT SIDE. May also be null if received on server side.
	 */
	public void receiveString(byte context, String message, @Nullable ServerPlayer sender);

}

package com.Da_Technomancer.crossroads.api.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Certain packets need to call or store code in render side only stuff, and can not do so without crashing due to (for example) WorldClient not existing on the server side.
 * In those cases, the packets should call methods in this class
 * This class is for CLIENT SIDE CODE ONLY
 *
 * Any changes to this class must be tested on a dedicated server to be accepted
 */
public class SafeCallable{

	public static Level getClientWorld(){
		return Minecraft.getInstance().level;
	}

	public static Player getClientPlayer(){
		return Minecraft.getInstance().player;
	}
}

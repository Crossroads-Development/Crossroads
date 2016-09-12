package com.Da_Technomancer.crossroads.API.packets;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.math.BlockPos;

/**Certain packets (such as SendLightningToClient) need to call code in WorldClient, and can not do so without crashing due to WorldClient not existing on the server side.
 * In those cases, the packets should call methods in this class
 */	
public class SafeCallable{
	
	protected static void summonLightning(WorldClient client, BlockPos pos){
		client.spawnEntityInWorld(new EntityLightningBolt(client, pos.getX(), pos.getY(), pos.getZ(), true));
	}
}

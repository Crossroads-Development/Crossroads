package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendDimLoadToClient;
import com.Da_Technomancer.crossroads.API.packets.StoreNBTToClient;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;

public final class EventHandlerServer{

	@SubscribeEvent
	public void syncServerDataToClient(ServerConnectionFromClientEvent e){
		int[] dims = DimensionManager.getDimensions(ModDimensions.workspaceDimType);
		//It has to be converted into a packet and sent directly through the manager because the player hasn't had a connection set at this point. 
		e.getManager().sendPacket(ModPackets.network.getPacketFrom(new SendDimLoadToClient(dims, true)));
	}
}

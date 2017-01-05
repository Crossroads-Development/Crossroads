package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.Main;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ModPackets{

	public static SimpleNetworkWrapper network;

	public static void preInit(){
		network = NetworkRegistry.INSTANCE.newSimpleChannel(Main.MODID + ".chan");

		int packetId = 5;
		network.registerMessage(SendIntToClient.class, SendIntToClient.class, packetId++, Side.CLIENT);
		network.registerMessage(SendStringToClient.class, SendStringToClient.class, packetId++, Side.CLIENT);
		network.registerMessage(SendPosToClient.class, SendPosToClient.class, packetId++, Side.CLIENT);
		network.registerMessage(SendDoubleToClient.class, SendDoubleToClient.class, packetId++, Side.CLIENT);
		network.registerMessage(SendElementNBTToClient.class, SendElementNBTToClient.class, packetId++, Side.CLIENT);
		network.registerMessage(SendLightningToClient.class, SendLightningToClient.class, packetId++, Side.CLIENT);
		network.registerMessage(SendBoolToClient.class, SendBoolToClient.class, packetId++, Side.CLIENT);
	}
}

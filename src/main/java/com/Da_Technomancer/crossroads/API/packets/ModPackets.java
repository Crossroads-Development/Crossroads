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
		network.registerMessage(SendDoubleToClient.class, SendDoubleToClient.class, packetId++, Side.CLIENT);
		network.registerMessage(StoreNBTToClient.class, StoreNBTToClient.class, packetId++, Side.CLIENT);
		network.registerMessage(SendChatToClient.class, SendChatToClient.class, packetId++, Side.CLIENT);
		network.registerMessage(SendFieldsToClient.class, SendFieldsToClient.class, packetId++, Side.CLIENT);
		network.registerMessage(SendMagicItemToServer.class, SendMagicItemToServer.class, packetId++, Side.SERVER);
		network.registerMessage(SendLooseBeamToClient.class, SendLooseBeamToClient.class, packetId++, Side.CLIENT);
		network.registerMessage(SendSlotFilterToClient.class, SendSlotFilterToClient.class, packetId++, Side.CLIENT);
		network.registerMessage(SendDimLoadToClient.class, SendDimLoadToClient.class, packetId++, Side.CLIENT);
		network.registerMessage(SendDoubleToServer.class, SendDoubleToServer.class, packetId++, Side.SERVER);
		network.registerMessage(SendIntToServer.class, SendIntToServer.class, packetId++, Side.SERVER);
		network.registerMessage(SendLogToClient.class, SendLogToClient.class, packetId++, Side.CLIENT);
		network.registerMessage(SendStringToServer.class, SendStringToServer.class, packetId++, Side.SERVER);
		network.registerMessage(SendNBTToClient.class, SendNBTToClient.class, packetId++, Side.CLIENT);
		network.registerMessage(SendPlayerTickCountToClient.class, SendPlayerTickCountToClient.class, packetId++, Side.CLIENT);
		network.registerMessage(SendDoubleArrayToServer.class, SendDoubleArrayToServer.class, packetId++, Side.SERVER);
		network.registerMessage(SendDoubleArrayToClient.class, SendDoubleArrayToClient.class, packetId++, Side.CLIENT);
		network.registerMessage(SendSpinToClient.class, SendSpinToClient.class, packetId++, Side.CLIENT);
		network.registerMessage(SendLooseArcToClient.class, SendLooseArcToClient.class, packetId++, Side.CLIENT);
		network.registerMessage(SendFlameInfoToClient.class, SendFlameInfoToClient.class, packetId++, Side.CLIENT);
		network.registerMessage(SendAlchNamesToClient.class, SendAlchNamesToClient.class, packetId++, Side.CLIENT);
	}
}

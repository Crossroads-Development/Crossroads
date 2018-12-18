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
		network.registerMessage(SendBeamItemToServer.class, SendBeamItemToServer.class, packetId++, Side.SERVER);
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
		network.registerMessage(AddVisualToClient.class, AddVisualToClient.class, packetId++, Side.CLIENT);
		network.registerMessage(NbtToEntityClient.class, NbtToEntityClient.class, packetId++, Side.CLIENT);
		network.registerMessage(NbtToEntityServer.class, NbtToEntityServer.class, packetId++, Side.SERVER);
		network.registerMessage(SendBiomeUpdateToClient.class, SendBiomeUpdateToClient.class, packetId++, Side.CLIENT);
		network.registerMessage(SendGoggleConfigureToServer.class, SendGoggleConfigureToServer.class, packetId++, Side.SERVER);
		network.registerMessage(SendLongToClient.class, SendLongToClient.class, packetId++, Side.CLIENT);
	}
}

package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.packets.Packet;
import com.Da_Technomancer.essentials.packets.PacketManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class CrossroadsPackets{

	public static SimpleChannel channel;
	private static int index = 0;

	public static void preInit(){
		channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(Crossroads.MODID, "channel"), () -> "1.0.0", (s) -> s.equals("1.0.0"), (s) -> s.equals("1.0.0"));
		registerPacket(SendIntToClient.class);
		registerPacket(SendStringToClient.class);
		registerPacket(SendDoubleToClient.class);
//		registerPacket(StoreNBTToClient.class);
		registerPacket(SendChatToClient.class);
		registerPacket(SendBeamItemToServer.class);
		registerPacket(SendDimLoadToClient.class);
		registerPacket(SendDoubleToServer.class);
		registerPacket(SendIntToServer.class);
		registerPacket(SendLogToClient.class);
		registerPacket(SendStringToServer.class);
		registerPacket(SendNBTToClient.class);
		registerPacket(SendPlayerTickCountToClient.class);
		registerPacket(SendDoubleArrayToServer.class);
		registerPacket(SendDoubleArrayToClient.class);
		registerPacket(SendSpinToClient.class);
		registerPacket(AddVisualToClient.class);
		registerPacket(NbtToEntityClient.class);
		registerPacket(NbtToEntityServer.class);
		registerPacket(SendBiomeUpdateToClient.class);
		registerPacket(SendGoggleConfigureToServer.class);
		registerPacket(SendLongToClient.class);
		registerPacket(SendTaylorToClient.class);
		registerPacket(SendMasterKeyToClient.class);
	}

	private static <T extends Packet> void registerPacket(Class<T> clazz){
		channel.registerMessage(index++, clazz, PacketManager::encode, (buf) -> PacketManager.decode(buf, clazz), PacketManager::activate);
	}
}

package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.packets.ClientPacket;
import com.Da_Technomancer.essentials.packets.Packet;
import com.Da_Technomancer.essentials.packets.PacketManager;
import com.Da_Technomancer.essentials.packets.ServerPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class CRPackets{

	public static SimpleChannel channel;
	private static int index = 0;

	public static void preInit(){
		channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(Crossroads.MODID, "channel"), () -> "1.0.0", (s) -> s.equals("1.0.0"), (s) -> s.equals("1.0.0"));
		registerPacket(SendIntToClient.class);
		registerPacket(SendStringToClient.class);
		registerPacket(SendDoubleToClient.class);
		registerPacket(StoreNBTToClient.class);
		registerPacket(SendChatToClient.class);
		registerPacket(SendBeamItemToServer.class);
		registerPacket(SendDimLoadToClient.class);
		registerPacket(SendDoubleToServer.class);
		registerPacket(SendIntToServer.class);
		registerPacket(SendLogToClient.class);
		registerPacket(SendStringToServer.class);
//		registerPacket(SendNBTToClient.class);
		registerPacket(SendPlayerTickCountToClient.class);
		registerPacket(SendDoubleArrayToServer.class);
		registerPacket(SendDoubleArrayToClient.class);
//		registerPacket(SendSpinToClient.class);
		registerPacket(AddVisualToClient.class);
		registerPacket(NbtToEntityClient.class);
		registerPacket(NbtToEntityServer.class);
		registerPacket(SendBiomeUpdateToClient.class);
		registerPacket(SendGoggleConfigureToServer.class);
//		registerPacket(SendLongToClient.class); moved to Essentials
		registerPacket(SendTaylorToClient.class);
		registerPacket(SendMasterKeyToClient.class);
	}

	private static <T extends Packet> void registerPacket(Class<T> clazz){
		channel.registerMessage(index++, clazz, PacketManager::encode, (buf) -> PacketManager.decode(buf, clazz), PacketManager::activate);
	}

	public static void sendPacketAround(World world, BlockPos pos, ClientPacket packet){
		channel.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), 512.0D, world.dimension.getType())), packet);
	}

	public static void sendPacketToPlayer(ServerPlayerEntity player, ClientPacket packet){
		channel.send(PacketDistributor.PLAYER.with(() -> player), packet);
	}

	public static void sendPacketToServer(ServerPacket packet){
		channel.sendToServer(packet);
	}

	public static void sendPacketToAll(ClientPacket packet){
		channel.send(PacketDistributor.ALL.noArg(), packet);
	}

	public static void sendPacketToDimension(World world, ClientPacket packet){
		channel.send(PacketDistributor.DIMENSION.with(() -> world.getDimension().getType()), packet);
	}
}

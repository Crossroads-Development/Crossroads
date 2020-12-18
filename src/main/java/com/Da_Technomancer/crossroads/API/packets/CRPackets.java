package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.packets.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.HashSet;

public class CRPackets{

	public static SimpleChannel channel;
	private static int index = 0;
	private static final HashSet<Class<? extends Packet>> registeredTypes = new HashSet<>(20);

	public static void preInit(){
		channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(Crossroads.MODID, "channel"), () -> "1.0.0", (s) -> s.equals("1.0.0"), (s) -> s.equals("1.0.0"));
		registerPacket(SendIntToClient.class);
		registerPacket(SendStringToClient.class);
		registerPacket(SendDoubleToClient.class);
//		registerPacket(StoreNBTToClient.class);
		registerPacket(SendChatToClient.class);
		registerPacket(SendBeamItemToServer.class);
//		registerPacket(SendDimLoadToClient.class);
		registerPacket(SendDoubleToServer.class);
		registerPacket(SendIntToServer.class);
//		registerPacket(SendLogToClient.class);
		registerPacket(SendStringToServer.class);
//		registerPacket(SendNBTToClient.class);
		registerPacket(SendPlayerTickCountToClient.class);
		registerPacket(SendDoubleArrayToServer.class);
		registerPacket(SendDoubleArrayToClient.class);
//		registerPacket(SendSpinToClient.class);
		registerPacket(AddVisualToClient.class);
		registerPacket(NbtToEntityClient.class);
//		registerPacket(NbtToEntityServer.class);
		registerPacket(SendBiomeUpdateToClient.class);
		registerPacket(SendGoggleConfigureToServer.class);
//		registerPacket(SendLongToClient.class); moved to Essentials
		registerPacket(SendTaylorToClient.class);
		registerPacket(SendMasterKeyToClient.class);
		registerPacket(SendElytraBoostToServer.class);
	}

	private static <T extends Packet> void registerPacket(Class<T> clazz){
		channel.registerMessage(index++, clazz, PacketManager::encode, (buf) -> PacketManager.decode(buf, clazz), PacketManager::activate);
		registeredTypes.add(clazz);
	}

	public static void sendPacketAround(World world, BlockPos pos, ClientPacket packet){
		if(world.isRemote){
			throw new IllegalStateException("Packet to client sent from client!");
		}
		//Check if this packet is registered with CR. If not, send it via the Essentials packet channel; this is done to make this method correct for all CR usage
		SimpleChannel messageChannel = registeredTypes.contains(packet.getClass()) ? channel : EssentialsPackets.channel;
		messageChannel.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), 512.0D, world.getDimensionKey())), packet);
	}

	public static void sendEffectPacketAround(World world, BlockPos pos, AddVisualToClient packet){
		if(world.isRemote){
			throw new IllegalStateException("Packet to client sent from client!");
		}
		//Check if this packet is registered with CR. If not, send it via the Essentials packet channel; this is done to make this method correct for all CR usage
		SimpleChannel messageChannel = registeredTypes.contains(packet.getClass()) ? channel : EssentialsPackets.channel;
		messageChannel.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), CRConfig.effectPacketDistance.get(), world.getDimensionKey())), packet);
	}

	public static void sendPacketToPlayer(ServerPlayerEntity player, ClientPacket packet){
		//Check if this packet is registered with CR. If not, send it via the Essentials packet channel; this is done to make this method correct for all CR usage
		SimpleChannel messageChannel = registeredTypes.contains(packet.getClass()) ? channel : EssentialsPackets.channel;
		messageChannel.send(PacketDistributor.PLAYER.with(() -> player), packet);
	}

	public static void sendPacketToServer(ServerPacket packet){
		//Check if this packet is registered with CR. If not, send it via the Essentials packet channel; this is done to make this method correct for all CR usage
		SimpleChannel messageChannel = registeredTypes.contains(packet.getClass()) ? channel : EssentialsPackets.channel;
		messageChannel.sendToServer(packet);
	}

	public static void sendPacketToAll(ClientPacket packet){
		//Check if this packet is registered with CR. If not, send it via the Essentials packet channel; this is done to make this method correct for all CR usage
		SimpleChannel messageChannel = registeredTypes.contains(packet.getClass()) ? channel : EssentialsPackets.channel;
		messageChannel.send(PacketDistributor.ALL.noArg(), packet);
	}

	public static void sendPacketToDimension(World world, ClientPacket packet){
		//Check if this packet is registered with CR. If not, send it via the Essentials packet channel; this is done to make this method correct for all CR usage
		SimpleChannel messageChannel = registeredTypes.contains(packet.getClass()) ? channel : EssentialsPackets.channel;
		messageChannel.send(PacketDistributor.DIMENSION.with(world::getDimensionKey), packet);
	}
}

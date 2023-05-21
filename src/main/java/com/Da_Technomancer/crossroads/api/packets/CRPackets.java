package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.essentials.api.packets.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public class CRPackets{

	public static SimpleChannel channel;
	private static int index = 0;
	private static final HashSet<Class<? extends Packet>> registeredTypes = new HashSet<>(20);

	public static void init(){
		channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(Crossroads.MODID, "channel"), () -> "1.0.0", (s) -> s.equals("1.0.0"), (s) -> s.equals("1.0.0"));
		//Create codecs for additional data types
		PacketManager.addCodec(int[].class, (val, buf) -> buf.writeVarIntArray((int[]) val), FriendlyByteBuf::readVarIntArray);
		PacketManager.addCodec(ParticleOptions.class,
				(Object val, FriendlyByteBuf buf) -> {
					ParticleOptions data = (ParticleOptions) val;
					buf.writeResourceLocation(Objects.requireNonNull(MiscUtil.getRegistryName(data.getType(), ForgeRegistries.PARTICLE_TYPES)));
					data.writeToNetwork(buf);
				},
				(FriendlyByteBuf buf) -> readParticleData(ForgeRegistries.PARTICLE_TYPES.getValue(buf.readResourceLocation()), buf)
		);
		PacketManager.addCodec(ResourceLocation.class, (val, buf) -> buf.writeResourceLocation((ResourceLocation) val), FriendlyByteBuf::readResourceLocation);
		PacketManager.addCodec(GlobalPos.class, (val, buf) -> buf.writeGlobalPos((GlobalPos) val), FriendlyByteBuf::readGlobalPos);
		PacketManager.addCodec(UUID.class, (val, buf) -> buf.writeUUID((UUID) val), FriendlyByteBuf::readUUID);

//		registerPacket(SendIntToClient.class);
//		registerPacket(SendStringToClient.class);
//		registerPacket(SendDoubleToClient.class);
//		registerPacket(StoreNBTToClient.class);
		registerPacket(SendChatToClient.class);
		registerPacket(SendBeamItemToServer.class);
//		registerPacket(SendDimLoadToClient.class);
//		registerPacket(SendDoubleToServer.class);
//		registerPacket(SendIntToServer.class);
//		registerPacket(SendLogToClient.class);
//		registerPacket(SendStringToServer.class);
//		registerPacket(SendNBTToClient.class);
		registerPacket(SendPlayerTickCountToClient.class);
//		registerPacket(SendDoubleArrayToServer.class);
//		registerPacket(SendDoubleArrayToClient.class);
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
		registerPacket(SendIntArrayToClient.class);
		registerPacket(CreateParticlesOnClient.class);
		registerPacket(SendCompassTargetToClient.class);
		registerPacket(SendLongToServer.class);
	}

	private static <T extends Packet> void registerPacket(Class<T> clazz){
		channel.registerMessage(index++, clazz, PacketManager::encode, (buf) -> PacketManager.decode(buf, clazz), PacketManager::activate);
		registeredTypes.add(clazz);
	}

	public static void sendPacketAround(Level world, BlockPos pos, ClientPacket packet){
		if(world.isClientSide){
			throw new IllegalStateException("Packet to client sent from client!");
		}
		//Check if this packet is registered with CR. If not, send it via the Essentials packet channel; this is done to make this method correct for all CR usage
		SimpleChannel messageChannel = registeredTypes.contains(packet.getClass()) ? channel : EssentialsPackets.channel;
		messageChannel.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), 512.0D, world.dimension())), packet);
	}

	public static void sendEffectPacketAround(Level world, BlockPos pos, AddVisualToClient packet){
		if(world.isClientSide){
			throw new IllegalStateException("Packet to client sent from client!");
		}
		//Check if this packet is registered with CR. If not, send it via the Essentials packet channel; this is done to make this method correct for all CR usage
		SimpleChannel messageChannel = registeredTypes.contains(packet.getClass()) ? channel : EssentialsPackets.channel;
		messageChannel.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), CRConfig.effectPacketDistance.get(), world.dimension())), packet);
	}

	public static void sendPacketToPlayer(ServerPlayer player, ClientPacket packet){
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

	public static void sendPacketToDimension(Level world, ClientPacket packet){
		//Check if this packet is registered with CR. If not, send it via the Essentials packet channel; this is done to make this method correct for all CR usage
		SimpleChannel messageChannel = registeredTypes.contains(packet.getClass()) ? channel : EssentialsPackets.channel;
		messageChannel.send(PacketDistributor.DIMENSION.with(world::dimension), packet);
	}

	private static <T extends ParticleOptions> T readParticleData(ParticleType<T> type, FriendlyByteBuf buf){
		if(type == null){
			return null;
		}
		return type.getDeserializer().fromNetwork(type, buf);
	}
}

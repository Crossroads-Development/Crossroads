package com.Da_Technomancer.crossroads.api.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class CRPackets{

	private static final double NEAR_RADIUS = 512.0D;

	@SubscribeEvent
	public static void registerPayloads(final RegisterPayloadHandlersEvent event){
		final PayloadRegistrar registrar = event.registrar("1");

		registrar.playToClient(SendChatToClient.TYPE, SendChatToClient.STREAM_CODEC, SendChatToClient::handlePacketClient);
		registrar.playToServer(SendBeamItemToServer.TYPE, SendBeamItemToServer.STREAM_CODEC, SendBeamItemToServer::handlePacketServer);
		registrar.playToClient(SendPlayerTickCountToClient.TYPE, SendPlayerTickCountToClient.STREAM_CODEC, SendPlayerTickCountToClient::handlePacketClient);
		registrar.playToClient(AddVisualToClient.TYPE, AddVisualToClient.STREAM_CODEC, AddVisualToClient::handlePacketClient);
		registrar.playToClient(NbtToEntityClient.TYPE, NbtToEntityClient.STREAM_CODEC, NbtToEntityClient::handlePacketClient);
		registrar.playToClient(SendBiomeUpdateToClient.TYPE, SendBiomeUpdateToClient.STREAM_CODEC, SendBiomeUpdateToClient::handlePacketClient);
		registrar.playToServer(SendGoggleConfigureToServer.TYPE, SendGoggleConfigureToServer.STREAM_CODEC, SendGoggleConfigureToServer::handlePacketServer);
		registrar.playToClient(SendTaylorToClient.TYPE, SendTaylorToClient.STREAM_CODEC, SendTaylorToClient::handlePacketClient);
		registrar.playToClient(SendMasterKeyToClient.TYPE, SendMasterKeyToClient.STREAM_CODEC, SendMasterKeyToClient::handlePacketClient);
		registrar.playToServer(SendElytraBoostToServer.TYPE, SendElytraBoostToServer.STREAM_CODEC, SendElytraBoostToServer::handlePacketServer);
		registrar.playToClient(SendIntArrayToClient.TYPE, SendIntArrayToClient.STREAM_CODEC, SendIntArrayToClient::handlePacketClient);
		registrar.playToClient(CreateParticlesOnClient.TYPE, CreateParticlesOnClient.STREAM_CODEC, CreateParticlesOnClient::handlePacketClient);
		registrar.playToClient(SendCompassTargetToClient.TYPE, SendCompassTargetToClient.STREAM_CODEC, SendCompassTargetToClient::handlePacketClient);
	}

	public static void sendPacketAround(Level world, BlockPos pos, CustomPacketPayload payload){
		// Previously there was an overload that took an AddVisualToClient payload, and allowed custom radius; I have
		// instead made that an optional param any payload may provide. Defaults same.
		sendPacketAround(world, pos, payload, NEAR_RADIUS);
	}

	public static void sendPacketAround(Level world, BlockPos pos, CustomPacketPayload payload, double radius){
		if(!(world instanceof ServerLevel serverLevel)){
			throw new IllegalStateException("Packet to client sent from client!");
		}

		PacketDistributor.sendToPlayersNear(serverLevel, null, pos.getX(), pos.getY(), pos.getZ(), radius, payload);
	}

	public static void sendPacketToPlayer(ServerPlayer player, CustomPacketPayload payload){
		PacketDistributor.sendToPlayer(player, payload);
	}

	public static void sendPacketToServer(CustomPacketPayload payload){
		PacketDistributor.sendToServer(payload);
	}

	public static void sendPacketToAll(CustomPacketPayload payload){
		PacketDistributor.sendToAllPlayers(payload);
	}

	public static void sendPacketToDimension(Level world, CustomPacketPayload payload){
		if(!(world instanceof ServerLevel serverLevel)){
			throw new IllegalStateException("Packet to clients sent from client!");
		}

		PacketDistributor.sendToPlayersInDimension(serverLevel, payload);
	}

	private static <T extends ParticleOptions> T readParticleData(ParticleType<T> type, RegistryFriendlyByteBuf buf){
		if(type == null){
			return null;
		}
		return type.streamCodec().decode(buf);
	}
}

package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.effects.alchemy_effects.AetherEffect;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SendBiomeUpdateToClient(BlockPos pos, String newBiome) implements CustomPacketPayload{

	public static final CustomPacketPayload.Type<SendBiomeUpdateToClient> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "send_biome_update_client"));

	public static final StreamCodec<ByteBuf, SendBiomeUpdateToClient> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, SendBiomeUpdateToClient::pos,
			ByteBufCodecs.STRING_UTF8, SendBiomeUpdateToClient::newBiome,
			SendBiomeUpdateToClient::new
	);

	public static void handlePacketClient(final SendBiomeUpdateToClient packet, final IPayloadContext context){
		context.enqueueWork(() -> {
			Level world;
			if((world = SafeCallable.getClientWorld()) != null){
				AetherEffect.setBiomeAtPos(world, packet.pos, AetherEffect.getBiomeHolder(ResourceLocation.withDefaultNamespace(packet.newBiome)));
			}
		});

	}

	// TODO: This should be client side only. Remove this message before final commit.

	@Override
	public Type<? extends CustomPacketPayload> type(){
		return TYPE;
	}
}

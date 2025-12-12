package com.Da_Technomancer.crossroads.api.packets;


import com.Da_Technomancer.crossroads.Crossroads;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record SendPlayerTickCountToClient(int tickCount) implements CustomPacketPayload{

	public static CustomPacketPayload.Type<SendPlayerTickCountToClient> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "send_player_tick_count_client"));

	public static StreamCodec<ByteBuf, SendPlayerTickCountToClient> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, SendPlayerTickCountToClient::tickCount,
			SendPlayerTickCountToClient::new
	);

	public static int playerTickCount = 0;//Only correct on the client side


	public static void handlePacketClient(final SendPlayerTickCountToClient packet, final IPayloadContext context){
		context.enqueueWork(() -> {
					playerTickCount += packet.tickCount;

				}
		);
	}

	@Override
	public Type<? extends CustomPacketPayload> type(){
		return TYPE;
	}
}

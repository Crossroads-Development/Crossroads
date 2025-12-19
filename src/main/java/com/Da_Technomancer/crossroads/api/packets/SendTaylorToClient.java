package com.Da_Technomancer.crossroads.api.packets;


import com.Da_Technomancer.crossroads.Crossroads;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Sends a Taylor series to the client. Used by Master Axes to reduce packet overhead
 */
public record SendTaylorToClient(long timestamp, float[] terms, BlockPos pos) implements CustomPacketPayload{

	public static final CustomPacketPayload.Type<SendTaylorToClient> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "send_taylor_client"));

	public static final StreamCodec<ByteBuf, SendTaylorToClient> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_LONG, SendTaylorToClient::timestamp,
			StreamCodecUtils.floatArrayStreamCodec(16), SendTaylorToClient::terms,
			BlockPos.STREAM_CODEC, SendTaylorToClient::pos,
			SendTaylorToClient::new
	);

	static void handlePacketClient(final SendTaylorToClient packet, final IPayloadContext context){
		context.enqueueWork(() -> {
			BlockEntity te = Minecraft.getInstance().level.getBlockEntity(packet.pos);
			if(te instanceof ITaylorReceiver taylorReceiver){
				taylorReceiver.receiveSeries(packet.timestamp, packet.terms);
			}
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type(){
		return TYPE;
	}
}

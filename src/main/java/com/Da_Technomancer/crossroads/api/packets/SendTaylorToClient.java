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

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Sends a Taylor series to the client. Used by Master Axes to reduce packet overhead
 */
public record SendTaylorToClient(long timestamp, ArrayList<Float> terms, BlockPos pos) implements CustomPacketPayload{
	public static final CustomPacketPayload.Type<SendTaylorToClient> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "send_taylor_client"));

	public static final StreamCodec<ByteBuf, SendTaylorToClient> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_LONG, SendTaylorToClient::timestamp,
			ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.FLOAT), SendTaylorToClient::terms,
			BlockPos.STREAM_CODEC, SendTaylorToClient::pos,
			SendTaylorToClient::new
	);

	public SendTaylorToClient(long timestamp, float[] terms, BlockPos pos){
		//TODO: it's more convenient to make a streamcodec for an ArrayList than an Array; should either use ofMember
		// and manually write buffer or switch to ArrayLists up/downstream from here.
		this(timestamp, new ArrayList(Arrays.asList(terms)), pos);
	}

	static void handlePacketClient(final SendTaylorToClient packet, final IPayloadContext context){
		context.enqueueWork(() -> {
			BlockEntity te = Minecraft.getInstance().level.getBlockEntity(packet.pos);
			if(te instanceof ITaylorReceiver){
				float[] terms = new float[4];
				for(int i = 0; i < 4; i++){
					terms[i] = packet.terms.get(i);
				}
				((ITaylorReceiver) te).receiveSeries(packet.timestamp, terms);
			}
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type(){
		return null;
	}
}

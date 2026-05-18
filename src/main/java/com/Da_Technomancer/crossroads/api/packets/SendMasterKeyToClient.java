package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.rotary.RotaryUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Used by Master Axes to reduce packet overhead
 */
public record SendMasterKeyToClient(int newKey) implements CustomPacketPayload{

	public static final CustomPacketPayload.Type<SendMasterKeyToClient> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "send_master_key_client"));

	public static final StreamCodec<ByteBuf, SendMasterKeyToClient> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, SendMasterKeyToClient::newKey,
			SendMasterKeyToClient::new
	);

	static void handlePacketClient(final SendMasterKeyToClient packet, final IPayloadContext context){
		context.enqueueWork(() -> {
			RotaryUtil.setMasterKey(packet.newKey, SafeCallable.getClientWorld());
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type(){
		return TYPE;
	}
}

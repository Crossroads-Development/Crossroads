package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.render.MultiLineMessageOverlay;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;

public record SendChatToClient(ArrayList<Component> chat, int id, BlockPos targetPos) implements CustomPacketPayload{
	public static final CustomPacketPayload.Type<SendChatToClient> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "send_chat_client"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SendChatToClient> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.collection(ArrayList::new, ComponentSerialization.STREAM_CODEC), SendChatToClient::chat,
			ByteBufCodecs.INT, SendChatToClient::id,
			BlockPos.STREAM_CODEC, SendChatToClient::targetPos,
			SendChatToClient::new
	);

	static void handlePacketClient(final SendChatToClient packet, final IPayloadContext context){
		context.enqueueWork(() -> {
			if(CRConfig.readoutChat.get()){
				//1.19.2: There used to be a system for deleting the old omnimeter messages to prevent spamming chat, but it's been removed
				for(Component component : packet.chat){
					SafeCallable.getClientPlayer().displayClientMessage(component, false);
				}
			}else{
				MultiLineMessageOverlay.setMessage(packet.chat, 60, packet.targetPos);
			}
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type(){
		return TYPE;
	}
}

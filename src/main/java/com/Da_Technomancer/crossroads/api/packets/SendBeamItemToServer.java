package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.technomancy.BeamUsingItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SendBeamItemToServer(byte[] newSetting) implements CustomPacketPayload{
	public static final CustomPacketPayload.Type<SendBeamItemToServer> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "send_beam_item_server"));

	public static final StreamCodec<ByteBuf, SendBeamItemToServer> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BYTE_ARRAY, SendBeamItemToServer::newSetting,
			SendBeamItemToServer::new
	);

	public static void handlePacketServer(final SendBeamItemToServer packet, final IPayloadContext context){
		context.enqueueWork(() -> {
			Player player = context.player();
			if(player != null){
				ItemStack stack;
				stack = player.getMainHandItem();
				if(!(stack.getItem() instanceof BeamUsingItem)){
					stack = player.getOffhandItem();
				}
				if(stack.getItem() instanceof BeamUsingItem){
					BeamUsingItem.setSetting(stack, packet.newSetting);
				}
			}
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type(){
		return TYPE;
	}
}

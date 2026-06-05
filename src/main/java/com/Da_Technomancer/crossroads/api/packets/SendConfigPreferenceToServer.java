package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.api.ConfigUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SendConfigPreferenceToServer(ConfigUtil.NumberTypes numberType) implements CustomPacketPayload{
	public static final Type<SendConfigPreferenceToServer> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "send_config_preference_to_server"));

	public static final StreamCodec<ByteBuf, SendConfigPreferenceToServer> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.idMapper(ordinal -> ConfigUtil.NumberTypes.values()[ordinal], ConfigUtil.NumberTypes::ordinal), SendConfigPreferenceToServer::numberType,
			SendConfigPreferenceToServer::new
	);

	public static void handlePacketServer(final SendConfigPreferenceToServer packet, final IPayloadContext context){
		context.enqueueWork(() -> {
			Player player = context.player();
			CRConfig.updateNumberFormatPreference(player, packet.numberType);
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type(){
		return TYPE;
	}
}

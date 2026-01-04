package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.EnumPath;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SendPathUnlockToServer(EnumPath path) implements CustomPacketPayload{

	public static final Type<SendPathUnlockToServer> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "send_path_unlock_to_server"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SendPathUnlockToServer> STREAM_CODEC = StreamCodec.composite(
			EnumPath.STREAM_CODEC, SendPathUnlockToServer::path,
			SendPathUnlockToServer::new
	);

	static void handlePacketServer(final SendPathUnlockToServer packet, final IPayloadContext context){
		context.enqueueWork(() -> {
			if(context.player() instanceof ServerPlayer player){
				//Verify that the player is eligible to unlock this path before unlocking it as a precaution against cracked clients
				if(EnumPath.canUnlock(player)){
					packet.path.setUnlocked(player, true);
				}
			}
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type(){
		return TYPE;
	}
}

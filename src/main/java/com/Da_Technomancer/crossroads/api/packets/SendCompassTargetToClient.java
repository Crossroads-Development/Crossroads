package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.witchcraft.BloodCompass;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.UUID;

/**
 * Syncs entity targeted information for the blood compass to the client
 */
public record SendCompassTargetToClient(GlobalPos targetPos, UUID targetUUID) implements CustomPacketPayload{
	public static final CustomPacketPayload.Type<SendCompassTargetToClient> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "send_compass_target_client"));

	public static StreamCodec<ByteBuf, SendCompassTargetToClient> STREAM_CODEC = StreamCodec.composite(
			GlobalPos.STREAM_CODEC, SendCompassTargetToClient::targetPos,
			UUIDUtil.STREAM_CODEC, SendCompassTargetToClient::targetUUID,
			SendCompassTargetToClient::new
	);


	static void handlePacketClient(final SendCompassTargetToClient packet, final IPayloadContext context){
		context.enqueueWork(() -> {
			CRItems.bloodCompass.syncedEntity = new BloodCompass.EntitySyncRecord(packet.targetUUID, packet.targetPos, SafeCallable.getClientWorld().getGameTime());
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type(){
		return TYPE;
	}
}

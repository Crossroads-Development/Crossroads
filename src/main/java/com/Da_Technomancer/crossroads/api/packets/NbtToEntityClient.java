package com.Da_Technomancer.crossroads.api.packets;


import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.api.packets.INBTReceiver;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record NbtToEntityClient(UUID entity, CompoundTag nbt) implements CustomPacketPayload{
	//TODO: this is never even served; it's used once outside this class, and that's registering it in CRPackets. Remove?

	public static CustomPacketPayload.Type<NbtToEntityClient> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "nbt_entity_client"));

	public static final StreamCodec<ByteBuf, NbtToEntityClient> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC, NbtToEntityClient::entity,
			ByteBufCodecs.COMPOUND_TAG, NbtToEntityClient::nbt,
			NbtToEntityClient::new
	);

	public static void handlePacketClient(final NbtToEntityClient packet, final IPayloadContext context){
		if(packet.entity == null){
			return;
		}
		Entity ent = null;
		for(Entity loadedEnt : Minecraft.getInstance().level.entitiesForRendering()){
			if(packet.entity.equals(loadedEnt.getUUID())){
				ent = loadedEnt;
				break;
			}
		}
		if(ent instanceof INBTReceiver){
			((INBTReceiver) ent).receiveNBT(packet.nbt, null);
		}
	}

	public static void handlePacketServer(final NbtToEntityClient packet, final IPayloadContext context){
		//TODO: see if this should use context.player().level() instead
		if(packet.entity == null){
			return;
		}
		Entity ent = null;
		for(Entity loadedEnt : Minecraft.getInstance().level.entitiesForRendering()){
			if(packet.entity.equals(loadedEnt.getUUID())){
				ent = loadedEnt;
				break;
			}
		}
		if(ent instanceof INBTReceiver){
			((INBTReceiver) ent).receiveNBT(packet.nbt, null);
		}
	}

	@Override
	public Type<? extends CustomPacketPayload> type(){
		return TYPE;
	}
}

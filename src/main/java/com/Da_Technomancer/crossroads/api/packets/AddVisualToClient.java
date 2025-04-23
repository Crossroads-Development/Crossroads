package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.api.render.IVisualEffect;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;

public record AddVisualToClient(CompoundTag nbt) implements CustomPacketPayload{

	public static final CustomPacketPayload.Type<AddVisualToClient> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "add_visual_to_client"));

	public static final StreamCodec<ByteBuf, AddVisualToClient> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.COMPOUND_TAG,
			AddVisualToClient::nbt,
			AddVisualToClient::new
	);

	public static final ArrayList<IVisualEffect> effectsToRender = new ArrayList<>();//Correct on client side only

	public static void handlePacketClient(final AddVisualToClient packet, final IPayloadContext context){
		context.enqueueWork(() -> {
			Level world;
			if((world = SafeCallable.getClientWorld()) != null){
				effectsToRender.add(CRRenderUtil.visualFactories[packet.nbt.getInt("id")].apply(world, packet.nbt));
			}
		});
	}

	// TODO: This should be client side only. Remove this message before final commit.


	@Override
	public Type<? extends CustomPacketPayload> type(){
		return TYPE;
	}
}

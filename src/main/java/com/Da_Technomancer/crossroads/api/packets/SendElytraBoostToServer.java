package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.technomancy.ArmorPropellerPack;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

public record SendElytraBoostToServer() implements CustomPacketPayload{
	public static final CustomPacketPayload.Type<SendElytraBoostToServer> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "send_elytra_boost_server"));

	private static final SendElytraBoostToServer SELF_INSTANCE = new SendElytraBoostToServer();

	public static final StreamCodec<RegistryFriendlyByteBuf, SendElytraBoostToServer> STREAM_CODEC = StreamCodec.unit(SELF_INSTANCE);

	static void handlePacketServer(final SendElytraBoostToServer packet, final IPayloadContext context){
		context.enqueueWork(() -> {
			if(context.player() instanceof ServerPlayer sender){
				ItemStack chestplate = sender.getItemBySlot(EquipmentSlot.CHEST);
				if(sender.isFallFlying() && chestplate.getItem() == CRItems.propellerPack && CRItems.propellerPack.getWindLevel(chestplate) > 0){
					CRItems.propellerPack.setWindLevel(chestplate, CRItems.propellerPack.getWindLevel(chestplate) - ArmorPropellerPack.WIND_PER_BOOST);
					ArmorPropellerPack.applyMidairBoost(sender);
				}
			}
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type(){
		return TYPE;
	}
}

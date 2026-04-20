package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.gui.container.DetailedCrafterContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.technomancy.ArmorGoggles;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nullable;

public record SendGoggleConfigureToServer(EnumGoggleLenses lens, boolean newSetting) implements CustomPacketPayload{
	public static final CustomPacketPayload.Type<SendGoggleConfigureToServer> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "send_goggle_configure_server"));

	public static final StreamCodec<ByteBuf, SendGoggleConfigureToServer> STREAM_CODEC = StreamCodec.composite(
			EnumGoggleLenses.STREAM_CODEC, SendGoggleConfigureToServer::lens,
			ByteBufCodecs.BOOL, SendGoggleConfigureToServer::newSetting,
			SendGoggleConfigureToServer::new
	);

	static void handlePacketServer(final SendGoggleConfigureToServer packet, final IPayloadContext context){
		context.enqueueWork(() -> {
			if(context.player() instanceof ServerPlayer player){
				ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
				ArmorGoggles.LensesSet lenses;
				if((stack.getItem() == CRItems.armorGoggles || stack.getItem() == CRItems.armorGogglesReinforced) && stack.has(CRItems.GOGGLE_LENSES_DATA) && (lenses = stack.get(CRItems.GOGGLE_LENSES_DATA)).lenses().containsKey(packet.lens)){
					if(EnumGoggleLenses.DIAMOND == packet.lens){
//					StoreNBTToClient.syncNBTToClient(player);//Sync player path data to client
						player.openMenu(GoggleProvider.INSTANCE, buf -> buf.writeBoolean(true));
					}
					if(packet.lens.requireEnableKey()){
						Object2BooleanMap<EnumGoggleLenses> newLenses = new Object2BooleanOpenHashMap<>(lenses.lenses());
						newLenses.put(packet.lens, packet.newSetting);
						stack.set(CRItems.GOGGLE_LENSES_DATA, new ArmorGoggles.LensesSet(newLenses));
					}
				}
			}
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type(){
		return TYPE;
	}

	private static class GoggleProvider implements MenuProvider{

		private static final GoggleProvider INSTANCE = new GoggleProvider();

		@Override
		public Component getDisplayName(){
			return Component.translatable("container.goggle_crafting");
		}

		@Nullable
		@Override
		public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
			FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
			buf.writeBoolean(true);//Encode that this is goggles
			return new DetailedCrafterContainer(id, playerInv, buf);
		}
	}
}

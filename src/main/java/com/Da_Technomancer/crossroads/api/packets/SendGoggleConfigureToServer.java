package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.gui.container.DetailedCrafterContainer;
import com.Da_Technomancer.crossroads.items.CRItems;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

public record SendGoggleConfigureToServer(String lensName, boolean newSetting) implements CustomPacketPayload{
	public static final CustomPacketPayload.Type<SendGoggleConfigureToServer> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "send_goggle_configure_server"));

	public static final StreamCodec<ByteBuf, SendGoggleConfigureToServer> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, SendGoggleConfigureToServer::lensName,
			ByteBufCodecs.BOOL, SendGoggleConfigureToServer::newSetting,
			SendGoggleConfigureToServer::new
	);

	static void handlePacketServer(final SendGoggleConfigureToServer packet, final IPayloadContext context){
		context.enqueueWork(() -> {
			if(context.player() instanceof ServerPlayer player){
				ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
				CompoundTag nbt = stack.getTag();
				if(stack.getItem() == CRItems.armorGoggles && nbt != null && nbt.contains(packet.lensName)){
					nbt.putBoolean(packet.lensName, packet.newSetting);

					if(EnumGoggleLenses.DIAMOND.toString().equals(packet.lensName)){
//					StoreNBTToClient.syncNBTToClient(player);//Sync player path data to client
						player.openMenu(GoggleProvider.INSTANCE, buf -> buf.writeBoolean(true));
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

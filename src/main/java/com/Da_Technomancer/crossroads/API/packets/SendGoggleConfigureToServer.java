package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.gui.container.DetailedCrafterContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.essentials.packets.ServerPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class SendGoggleConfigureToServer extends ServerPacket{

	public String lensName;
	public boolean newSetting;

	private static final Field[] FIELDS = fetchFields(SendGoggleConfigureToServer.class, "lensName", "newSetting");

	public SendGoggleConfigureToServer(){

	}

	public SendGoggleConfigureToServer(EnumGoggleLenses lens, boolean setting){
		this.lensName = lens.toString();
		this.newSetting = setting;
	}

	@Nonnull
	@Override
	protected Field[] getFields(){
		return FIELDS;
	}

	@Override
	protected void run(@Nullable ServerPlayer player){
		if(player != null){
			ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
			CompoundTag nbt = stack.getTag();
			if(stack.getItem() == CRItems.armorGoggles && nbt != null && nbt.contains(lensName)){
				nbt.putBoolean(lensName, newSetting);

				if(EnumGoggleLenses.DIAMOND.toString().equals(lensName)){
//					StoreNBTToClient.syncNBTToClient(player);//Sync player path data to client
					NetworkHooks.openGui(player, GoggleProvider.INSTANCE, buf -> buf.writeBoolean(true));
				}
			}
		}
	}

	private static class GoggleProvider implements MenuProvider{

		private static final GoggleProvider INSTANCE = new GoggleProvider();

		@Override
		public Component getDisplayName(){
			return new TranslatableComponent("container.goggle_crafting");
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

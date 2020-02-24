package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.gui.container.DetailedCrafterContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.essentials.packets.ServerPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkHooks;

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
		this.lensName = lens.name();
		this.newSetting = setting;
	}

	@Nonnull
	@Override
	protected Field[] getFields(){
		return FIELDS;
	}

	@Override
	protected void run(@Nullable ServerPlayerEntity player){
		if(player != null){
			ItemStack stack = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
			if(stack.getItem() == CRItems.moduleGoggles && stack.hasTag() && stack.getTag().contains(lensName)){
				stack.getTag().putBoolean(lensName, newSetting);

				if(EnumGoggleLenses.DIAMOND.name().equals(lensName)){
//					StoreNBTToClient.syncNBTToClient(player);//Sync player path data to client
					NetworkHooks.openGui(player, GoggleProvider.INSTANCE, buf -> buf.writeBoolean(false));
				}
			}
		}
	}

	private static class GoggleProvider implements INamedContainerProvider{

		private static final GoggleProvider INSTANCE = new GoggleProvider();

		@Override
		public ITextComponent getDisplayName(){
			return new TranslationTextComponent("container.goggle_crafting");
		}

		@Nullable
		@Override
		public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player){
			PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
			buf.writeBoolean(true);//Encode that this is goggles
			return new DetailedCrafterContainer(id, playerInv, buf);
		}
	}
}

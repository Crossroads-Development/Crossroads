package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.gui.GuiHandler;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.essentials.packets.Message;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendGoggleConfigureToServer extends Message<SendGoggleConfigureToServer>{

	public SendGoggleConfigureToServer(){

	}

	public String lensName;
	public boolean newSetting;

	public SendGoggleConfigureToServer(EnumGoggleLenses lens, boolean setting){
		this.lensName = lens.name();
		this.newSetting = setting;
	}

	@Override
	public IMessage handleMessage(MessageContext context){
		if(context.side != Side.SERVER){
			System.err.println("MessageToServer received on wrong side:" + context.side);
			return null;
		}
		EntityPlayerMP player = context.getServerHandler().player;
		if(player == null){
			System.err.println("Player was null on packet arrival");
			return null;
		}
		player.getServerWorld().addScheduledTask(new Runnable(){
			@Override
			public void run(){
				processMessage(player, lensName, newSetting);
			}
		});

		return null;
	}

	public void processMessage(EntityPlayer player, String lens, boolean setting){
		ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		if(stack.getItem() == ModItems.moduleGoggles && stack.hasTagCompound() && stack.getTagCompound().hasKey(lens)){
			stack.getTagCompound().setBoolean(lens, setting);

			if(EnumGoggleLenses.DIAMOND.name().equals(lens)){
				StoreNBTToClient.syncNBTToClient((EntityPlayerMP) player, false);
				player.openGui(Main.instance, GuiHandler.FAKE_CRAFTER_GUI, player.world, (int) player.posX, (int) player.posY, (int) player.posZ);
			}
		}
	}
}

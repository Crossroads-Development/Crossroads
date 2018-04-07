package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This can be used to store an NBTTagCompound on a client. 
 * The most recent one received overwrites the previous. This is used for GUIs mainly. 
 */
@SuppressWarnings("serial")
public class StoreNBTToClient extends Message<StoreNBTToClient>{

	/**
	 * Syncs the NBTTagCompound from MiscOp.getPlayerTag(player), as well as the synced configs if config is true.
	 */
	public static void syncNBTToClient(EntityPlayerMP player, boolean config){
		ModPackets.network.sendTo(new StoreNBTToClient(MiscOp.getPlayerTag(player), false), player);
		if(config){
			ModPackets.network.sendTo(new StoreNBTToClient(ModConfig.nbtToSyncConfig(), true), player);
		}
	}

	public StoreNBTToClient(){

	}

	public static NBTTagCompound clientPlayerTag = new NBTTagCompound();

	public NBTTagCompound nbt;
	public boolean config;

	public StoreNBTToClient(NBTTagCompound nbt, boolean config){
		this.nbt = nbt;
		this.config = config;
	}

	@Override
	public IMessage handleMessage(MessageContext context){
		if(context.side != Side.CLIENT){
			Main.logger.error("MessageToClient received on wrong side:" + context.side);
			return null;
		}

		Minecraft minecraft = Minecraft.getMinecraft();
		minecraft.addScheduledTask(() -> {
			if(config){
				ModConfig.syncPropNBT = nbt;
			}else{
				clientPlayerTag = nbt;
			}
		});
		return null;
	}
}

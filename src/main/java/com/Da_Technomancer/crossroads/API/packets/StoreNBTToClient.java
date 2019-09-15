package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.CrossroadsConfig;
import com.Da_Technomancer.essentials.packets.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This can be used to store an NBTTagCompound on a render.
 * The most recent one received overwrites the previous. This is used for GUIs mainly. 
 */
@SuppressWarnings("serial")
public class StoreNBTToClient extends Message<StoreNBTToClient>{

	/**
	 * Syncs the NBTTagCompound from MiscUtil.getPlayerTag(player), as well as the synced configs if config is true.
	 */
	public static void syncNBTToClient(ServerPlayerEntity player, boolean config){
		ModPackets.network.sendTo(new StoreNBTToClient(MiscUtil.getPlayerTag(player), false), player);
		if(config){
			ModPackets.network.sendTo(new StoreNBTToClient(CrossroadsConfig.nbtToSyncConfig(), true), player);
		}
	}

	public StoreNBTToClient(){

	}

	public static CompoundNBT clientPlayerTag = new CompoundNBT();

	public CompoundNBT nbt;
	public boolean config;

	public StoreNBTToClient(CompoundNBT nbt, boolean config){
		this.nbt = nbt;
		this.config = config;
	}

	@Override
	public IMessage handleMessage(MessageContext context){
		if(context.side != Side.CLIENT){
			Crossroads.logger.error("MessageToClient received on wrong side:" + context.side);
			return null;
		}

		Minecraft minecraft = Minecraft.getInstance();
		minecraft.addScheduledTask(() -> {
			if(config){
				CrossroadsConfig.syncPropNBT = nbt;
			}else{
				clientPlayerTag = nbt;
			}
		});
		return null;
	}
}

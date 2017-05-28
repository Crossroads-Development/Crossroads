package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.ModConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SyncConfigsToClient extends Message<SyncConfigsToClient>{

	public SyncConfigsToClient(){
		
	}
	
	public NBTTagCompound nbt;

	public SyncConfigsToClient(NBTTagCompound nbt){
		this.nbt = nbt;
	}

	@Override
	public IMessage handleMessage(MessageContext context){
		if(context.side != Side.CLIENT){
			System.err.println("MessageToClient received on wrong side:" + context.side);
			return null;
		}

		Minecraft minecraft = Minecraft.getMinecraft();
		minecraft.addScheduledTask(new Runnable(){
			public void run(){
				processMessage(nbt);
			}
		});
		return null;
	}

	public void processMessage(NBTTagCompound nbt){
		ModConfig.syncPropNBT = nbt;
	}
}

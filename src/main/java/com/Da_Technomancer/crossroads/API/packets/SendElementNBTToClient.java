package com.Da_Technomancer.crossroads.API.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendElementNBTToClient extends Message<SendElementNBTToClient>{

	public SendElementNBTToClient(){
		
	}
	
	public static NBTTagCompound elementNBT = new NBTTagCompound();
	
	public NBTTagCompound nbt;

	public SendElementNBTToClient(NBTTagCompound nbt){
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
		elementNBT = nbt;
	}
}

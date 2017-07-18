package com.Da_Technomancer.crossroads.API.packets;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendPlayerTickCountToClient extends Message<SendPlayerTickCountToClient>{

	public SendPlayerTickCountToClient(){
		
	}

	public int tickCount;

	public SendPlayerTickCountToClient(int tickCount){
		this.tickCount = tickCount;
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
				SafeCallable.playerTickCount = tickCount;
			}
		});

		return null;
	}
}

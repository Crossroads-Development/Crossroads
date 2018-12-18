package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.essentials.packets.Message;
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
			Main.logger.error("MessageToClient received on wrong side:" + context.side);
			return null;
		}

		Minecraft minecraft = Minecraft.getMinecraft();
		minecraft.addScheduledTask(new Runnable(){
			public void run(){
				if(tickCount > 0){
					SafeCallable.playerTickCount += tickCount - 1;
				}else{
					SafeCallable.playerTickCount = 0;
				}
			}
		});

		return null;
	}
}

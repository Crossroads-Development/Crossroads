package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.API.technomancy.FieldWorldSavedData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendFieldDisableToClient extends Message<SendFieldDisableToClient>{

	public SendFieldDisableToClient(){
	}

	public long chunk;

	public SendFieldDisableToClient(long chunk){
		this.chunk = chunk;
	}

	@Override
	public IMessage handleMessage(MessageContext context){
		if(context.side != Side.CLIENT){
			System.err.println("MessageToClient received on wrong side:" + context.side);
			return null;
		}

		Minecraft minecraft = Minecraft.getMinecraft();
		final WorldClient worldClient = minecraft.world;
		minecraft.addScheduledTask(new Runnable(){
			public void run(){
				processMessage(worldClient, chunk);
			}
		});

		return null;
	}

	public void processMessage(World worldClient, long chunk){
		FieldWorldSavedData.get(worldClient).fieldNodes.remove(chunk);
	}
}

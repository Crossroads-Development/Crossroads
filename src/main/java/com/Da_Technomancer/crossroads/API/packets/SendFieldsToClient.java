package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.API.fields.FieldWorldSavedData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendFieldsToClient extends Message<SendFieldsToClient>{
	
	public SendFieldsToClient(){
	}
	
	public byte[][] fieldLayer;
	public byte type;
	public long chunk;
	
	public SendFieldsToClient(byte[][] fieldLayer, byte type, long chunk){
		this.fieldLayer = fieldLayer;
		this.type = type;
		this.chunk = chunk;
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
				processMessage(fieldLayer, type, chunk, minecraft.theWorld);
			}
		});

		return null;
	}

	public void processMessage(byte[][] fieldLayer, byte type, long chunk, WorldClient world){
		FieldWorldSavedData saved = FieldWorldSavedData.get(world);
		if(!saved.fieldNodes.containsKey(chunk)){
			saved.fieldNodes.put(chunk, FieldWorldSavedData.getDefaultChunkFlux());
		}
		saved.fieldNodes.get(chunk)[type] = fieldLayer;
	}
}

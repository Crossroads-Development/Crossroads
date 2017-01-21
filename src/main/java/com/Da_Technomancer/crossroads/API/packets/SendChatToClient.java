package com.Da_Technomancer.crossroads.API.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendChatToClient extends Message<SendChatToClient>{
	
	public SendChatToClient(){
	}
	
	public String chat;
	public int id;
	
	public SendChatToClient(String chat, int id){
		this.chat = chat;
		this.id = id;
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
				processMessage(chat, id);
			}
		});

		return null;
	}

	public void processMessage(String chat, int id){
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(chat), id);
	}
}

package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.packets.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.InvocationTargetException;

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
			Crossroads.logger.error("MessageToClient received on wrong side:" + context.side);
			return null;
		}

		Minecraft minecraft = Minecraft.getInstance();
		minecraft.addScheduledTask(new Runnable(){
			public void run(){
				processMessage(chat, id);
			}
		});

		return null;
	}

	public void processMessage(String chat, int id){
		NewChatGui chatGui = Minecraft.getInstance().ingameGUI.getChatGUI();
		if(SafeCallable.printChatNoLog == null){
			chatGui.printChatMessageWithOptionalDeletion(new StringTextComponent(chat), id);
		}else{
			try{
				SafeCallable.printChatNoLog.invoke(chatGui, new StringTextComponent(chat), id, Minecraft.getInstance().ingameGUI.getUpdateCounter(), false);
			}catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
				Crossroads.logger.catching(e);
			}
		}
	}
}

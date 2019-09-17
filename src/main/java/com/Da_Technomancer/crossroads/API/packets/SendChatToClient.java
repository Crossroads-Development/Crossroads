package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.packets.ClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class SendChatToClient extends ClientPacket{

	public String chat;
	public int id;

	private static final Field[] FIELDS = fetchFields(SendChatToClient.class, "chat", "id");

	@SuppressWarnings("unused")
	public SendChatToClient(){

	}

	public SendChatToClient(String chat, int id){
		this.chat = chat;
		this.id = id;
	}

	@Nonnull
	@Override
	protected Field[] getFields(){
		return FIELDS;
	}

	@Override
	protected void run(){
		NewChatGui chatGui = Minecraft.getInstance().ingameGUI.getChatGUI();
		if(SafeCallable.printChatNoLog == null){
			chatGui.printChatMessageWithOptionalDeletion(new StringTextComponent(chat), id);
		}else{
			try{
				SafeCallable.printChatNoLog.invoke(chatGui, new StringTextComponent(chat), id, Minecraft.getInstance().ingameGUI.getTicks(), false);
			}catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
				Crossroads.logger.catching(e);
			}
		}
	}
}

package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.packets.ClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class SendChatToClient extends ClientPacket{

	public String chat;
	public int id;

	private static final Field[] FIELDS = fetchFields(SendChatToClient.class, "chat", "id");

	//An arbitrarily chosen unicode character to divide the serialized ITextComponents in the string
	//Why a Ϫ? I just thought it looked neat.
	private static final char DIVIDER = '\u1002';

	@SuppressWarnings("unused")
	public SendChatToClient(){

	}

	public SendChatToClient(List<Component> chat, int id){
		StringBuilder s = new StringBuilder();
		for(Component comp : chat){
			s.append(DIVIDER);
			s.append(Component.Serializer.toJson(comp));
		}

		this.chat = s.toString();
		this.id = id;
	}

	@Nonnull
	@Override
	protected Field[] getFields(){
		return FIELDS;
	}

	@Override
	protected void run(){
		List<Component> components = new ArrayList<>();

		String active = chat;
		while(active.length() != 0){
			active = active.substring(1);//Delete divider char
			int nextInd = active.indexOf(DIVIDER);
			if(nextInd == -1){
				nextInd = active.length();
			}
			components.add(Component.Serializer.fromJsonLenient(active.substring(0, nextInd)));
			if(nextInd + 1 < active.length()){
				active = active.substring(nextInd);
			}else{
				active = "";
			}
		}

		Component combined;
		StringBuilder combo = new StringBuilder();
		for(int i = 0; i < components.size(); i++){
			combo.append(components.get(i).getString());
			if(i + 1 < components.size()){
				combo.append("§f\n");
			}
		}
		combined = new TextComponent(combo.toString());

		ChatComponent chatGui = Minecraft.getInstance().gui.getChat();
		if(SafeCallable.getPrintChatNoLog() == null){
			chatGui.addMessage(combined);
		}else{
			//Print it to the chat without logging it, to avoid flooding the log
			try{
				SafeCallable.getPrintChatNoLog().invoke(chatGui, combined, id, Minecraft.getInstance().gui.getGuiTicks(), false);
			}catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
				Crossroads.logger.catching(e);
			}
		}
	}
}

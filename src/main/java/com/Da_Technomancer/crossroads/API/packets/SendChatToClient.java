package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.packets.ClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

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

	public SendChatToClient(List<ITextComponent> chat, int id){
		StringBuilder s = new StringBuilder();
		for(ITextComponent comp : chat){
			s.append(DIVIDER);
			s.append(ITextComponent.Serializer.toJson(comp));
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
		List<ITextComponent> components = new ArrayList<>();

		String active = chat;
		while(active.length() != 0){
			active = active.substring(1);//Delete divider char
			int nextInd = active.indexOf(DIVIDER);
			if(nextInd == -1){
				nextInd = active.length();
			}
			components.add(ITextComponent.Serializer.func_240644_b_(active.substring(0, nextInd)));
			if(nextInd + 1 < active.length()){
				active = active.substring(nextInd);
			}else{
				active = "";
			}
		}

		ITextComponent combined;
		StringBuilder combo = new StringBuilder();
		for(int i = 0; i < components.size(); i++){
			combo.append(components.get(i).getString());
			if(i + 1 < components.size()){
				combo.append("\n");
			}
		}
		combined = new StringTextComponent(combo.toString());

		NewChatGui chatGui = Minecraft.getInstance().ingameGUI.getChatGUI();
		if(SafeCallable.getPrintChatNoLog() == null){
			chatGui.printChatMessage(combined);
		}else{
			//Print it to the chat without logging it, to avoid flooding the log
			try{
				SafeCallable.getPrintChatNoLog().invoke(chatGui, combined, id, Minecraft.getInstance().ingameGUI.getTicks(), false);
			}catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
				Crossroads.logger.catching(e);
			}
		}
	}
}

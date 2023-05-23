package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.render.MultiLineMessageOverlay;
import com.Da_Technomancer.essentials.api.packets.ClientPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SendChatToClient extends ClientPacket{

	public String chat;
	public int id;
	public BlockPos targetPos;

	private static final Field[] FIELDS = fetchFields(SendChatToClient.class, "chat", "id", "targetPos");

	//An arbitrarily chosen unicode character to divide the serialized ITextComponents in the string
	//Why a Ϫ? I just thought it looked neat.
	private static final char DIVIDER = '\u1002';

	@SuppressWarnings("unused")
	public SendChatToClient(){

	}

	public SendChatToClient(List<Component> chat, int id, BlockPos targetPos){
		StringBuilder s = new StringBuilder();
		for(Component comp : chat){
			s.append(DIVIDER);
			s.append(Component.Serializer.toJson(comp));
		}

		this.chat = s.toString();
		this.id = id;
		this.targetPos = targetPos;
	}

	@Nonnull
	@Override
	protected Field[] getFields(){
		return FIELDS;
	}

	@Override
	protected void run(){
		ArrayList<Component> components = new ArrayList<>();

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

//		//Combine into single multi-line component
//		//Support for multi-line components is generally poor
//		Component combined;
//		StringBuilder combo = new StringBuilder();
//		for(int i = 0; i < components.size(); i++){
//			combo.append(components.get(i).getString());
//			if(i + 1 < components.size()){
//				combo.append("§f\n");
//			}
//		}
//		combined = Component.literal(combo.toString());

		if(CRConfig.readoutChat.get()){
			//1.19.2: There used to be a system for deleting the old omnimeter messages to prevent spamming chat, but it's been removed
			for(Component component : components){
				SafeCallable.getClientPlayer().displayClientMessage(component, false);
			}
		}else{
			MultiLineMessageOverlay.setMessage(components, 60, targetPos);
		}
	}
}

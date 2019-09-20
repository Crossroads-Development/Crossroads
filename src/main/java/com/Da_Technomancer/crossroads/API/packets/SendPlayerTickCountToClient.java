package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.essentials.packets.ClientPacket;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class SendPlayerTickCountToClient extends ClientPacket{

	public int tickCount;

	private static final Field[] FIELDS = fetchFields(SendPlayerTickCountToClient.class, "tickCount");

	@SuppressWarnings("unused")
	public SendPlayerTickCountToClient(){

	}

	public SendPlayerTickCountToClient(int tickCount){
		this.tickCount = tickCount;
	}

	@Nonnull
	@Override
	protected Field[] getFields(){
		return FIELDS;
	}

	@Override
	protected void run(){
		if(tickCount > 0){
			SafeCallable.playerTickCount += tickCount - 1;
		}else{
			SafeCallable.playerTickCount = 0;
		}
	}
}

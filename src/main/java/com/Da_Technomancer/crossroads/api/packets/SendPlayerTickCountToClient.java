package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.essentials.api.packets.ClientPacket;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class SendPlayerTickCountToClient extends ClientPacket{

	public static int playerTickCount = 0;//Only correct on the client side

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
		playerTickCount += tickCount;
	}
}

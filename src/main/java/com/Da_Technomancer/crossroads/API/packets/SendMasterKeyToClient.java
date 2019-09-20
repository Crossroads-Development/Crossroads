package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.essentials.packets.ClientPacket;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

/**
 * Sends a Taylor series to the client. Used by Master Axes to reduce packet overhead
 */
public class SendMasterKeyToClient extends ClientPacket{

	public int newKey;

	private static final Field[] FIELDS = fetchFields(SendMasterKeyToClient.class, "newKey");

	@SuppressWarnings("unused")
	public SendMasterKeyToClient(){

	}

	public SendMasterKeyToClient(int newMasterKey){
		newKey = newMasterKey;
	}

	@Nonnull
	@Override
	protected Field[] getFields(){
		return FIELDS;
	}

	@Override
	protected void run(){
		RotaryUtil.setMasterKey(newKey);
	}
}

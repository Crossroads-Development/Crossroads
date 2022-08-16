package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.witchcraft.BloodCompass;
import com.Da_Technomancer.essentials.api.packets.ClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.GlobalPos;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.UUID;

/**
 * Syncs entity targeted information for the blood compass to the client
 */
public class SendCompassTargetToClient extends ClientPacket{

	public GlobalPos targetPos;
	public UUID targetUUID;

	private static final Field[] FIELDS = fetchFields(SendCompassTargetToClient.class, "targetPos", "targetUUID");

	@SuppressWarnings("unused")
	public SendCompassTargetToClient(){

	}

	public SendCompassTargetToClient(GlobalPos targetPos, UUID targetUUID){
		this.targetPos = targetPos;
		this.targetUUID = targetUUID;
	}

	@Nonnull
	@Override
	protected Field[] getFields(){
		return FIELDS;
	}

	@Override
	protected void run(){
		CRItems.bloodCompass.syncedEntity = new BloodCompass.EntitySyncRecord(targetUUID, targetPos, Minecraft.getInstance().level.getGameTime());
	}
}

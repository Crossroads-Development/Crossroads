package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.essentials.api.packets.ClientPacket;
import com.Da_Technomancer.essentials.api.packets.INBTReceiver;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.UUID;

public class NbtToEntityClient extends ClientPacket{

	public UUID entity;
	public CompoundTag nbt;

	private static final Field[] FIELDS = fetchFields(NbtToEntityClient.class, "entity", "nbt");

	@SuppressWarnings("unused")
	public NbtToEntityClient(){
		
	}

	public NbtToEntityClient(UUID entity, CompoundTag nbt){
		this.entity = entity;
		this.nbt = nbt;
	}

	@Nonnull
	@Override
	protected Field[] getFields(){
		return FIELDS;
	}

	@Override
	protected void run(){
		if(entity == null){
			return;
		}
		Entity ent = null;
		for(Entity loadedEnt : Minecraft.getInstance().level.entitiesForRendering()){
			if(entity.equals(loadedEnt.getUUID())){
				ent = loadedEnt;
				break;
			}
		}
		if(ent instanceof INBTReceiver){
			((INBTReceiver) ent).receiveNBT(nbt, null);
		}
	}
}

package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.essentials.packets.ClientPacket;
import com.Da_Technomancer.essentials.packets.INBTReceiver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.UUID;

public class NbtToEntityClient extends ClientPacket{

	public UUID entity;
	public CompoundNBT nbt;

	private static final Field[] FIELDS = fetchFields(NbtToEntityClient.class, "entity", "nbt");

	@SuppressWarnings("unused")
	public NbtToEntityClient(){
		
	}

	public NbtToEntityClient(UUID entity, CompoundNBT nbt){
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
		ClientWorld worldClient = Minecraft.getInstance().world;
		if(worldClient == null || entity == null){
			return;
		}
		Entity ent = null;
		for(Entity loadedEnt : worldClient.getAllEntities()){
			if(entity.equals(loadedEnt.getUniqueID())){
				ent = loadedEnt;
				break;
			}
		}
		if(ent instanceof INBTReceiver){
			((INBTReceiver) ent).receiveNBT(nbt, null);
		}
	}
}

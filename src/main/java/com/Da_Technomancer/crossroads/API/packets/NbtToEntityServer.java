package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.essentials.packets.INBTReceiver;
import com.Da_Technomancer.essentials.packets.ServerPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.UUID;

public class NbtToEntityServer extends ServerPacket{

	public UUID entity;
	public CompoundNBT nbt;
	public int dim;

	private static final Field[] FIELDS = fetchFields(NbtToEntityServer.class, "entity", "nbt", "dim");

	@SuppressWarnings("unused")
	public NbtToEntityServer(){

	}

	public NbtToEntityServer(UUID entity, int dim, CompoundNBT nbt){
		this.entity = entity;
		this.nbt = nbt;
		this.dim = dim;
	}

	@Nonnull
	@Override
	protected Field[] getFields(){
		return FIELDS;
	}

	@Override
	protected void run(@Nullable ServerPlayerEntity serverPlayerEntity){
		ClientWorld world = Minecraft.getInstance().world;
		if(world == null || entity == null){
			return;
		}
		Entity ent = null;

		for(Entity loadedEnt : world.getAllEntities()){
			if(entity.equals(loadedEnt.getUniqueID())){
				ent = loadedEnt;
				break;
			}
		}
		if(ent instanceof INBTReceiver){
			((INBTReceiver) ent).receiveNBT(nbt, serverPlayerEntity);
		}
	}
}

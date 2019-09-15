package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.packets.Message;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.ServerWorld;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

@SuppressWarnings("serial")
public class NbtToEntityServer extends Message<NbtToEntityServer>{

	public NbtToEntityServer(){

	}

	public UUID entity;
	public CompoundNBT nbt;
	public int dim;

	public NbtToEntityServer(UUID entity, int dim, CompoundNBT nbt){
		this.entity = entity;
		this.nbt = nbt;
		this.dim = dim;
	}

	@Override
	public IMessage handleMessage(MessageContext context){
		if(context.side != Side.SERVER){
			Crossroads.logger.error("MessageToServer received on wrong side:" + context.side);
			return null;
		}

		DimensionManager.getWorld(dim).addScheduledTask(new Runnable(){
			@Override
			public void run(){
				processMessage(DimensionManager.getWorld(dim), entity, nbt);
			}
		});

		return null;
	}

	public void processMessage(ServerWorld world, UUID entity, CompoundNBT nbt){
		if(world == null || entity == null){
			return;
		}
		Entity ent = null;

		for(Entity loadedEnt : world.loadedEntityList){
			if(entity.equals(loadedEnt.getUniqueID())){
				ent = loadedEnt;
				break;
			}
		}
		if(ent instanceof INbtReceiver){
			((INbtReceiver) ent).receiveNBT(nbt);
		}
	}
}

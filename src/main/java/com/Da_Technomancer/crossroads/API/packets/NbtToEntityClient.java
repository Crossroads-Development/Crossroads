package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.essentials.packets.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

@SuppressWarnings("serial")
public class NbtToEntityClient extends Message<NbtToEntityClient>{

	public NbtToEntityClient(){
		
	}

	public UUID entity;
	public NBTTagCompound nbt;

	public NbtToEntityClient(UUID entity, NBTTagCompound nbt){
		this.entity = entity;
		this.nbt = nbt;
	}

	@Override
	public IMessage handleMessage(MessageContext context){
		if(context.side != Side.CLIENT){
			System.err.println("MessageToClient received on wrong side:" + context.side);
			return null;
		}

		Minecraft minecraft = Minecraft.getMinecraft();
		final WorldClient worldClient = minecraft.world;
		minecraft.addScheduledTask(new Runnable(){
			public void run(){
				processMessage(worldClient, entity, nbt);
			}
		});

		return null;
	}

	public void processMessage(WorldClient worldClient, UUID entity, NBTTagCompound nbt){
		if(worldClient == null || entity == null){
			return;
		}
		Entity ent = null;
		for(Entity loadedEnt : worldClient.getLoadedEntityList()){
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

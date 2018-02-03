package com.Da_Technomancer.crossroads.API.packets;

import java.util.UUID;

import com.Da_Technomancer.crossroads.entity.EntityFlame;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendFlameInfoToClient extends Message<SendFlameInfoToClient>{

	public SendFlameInfoToClient(){
		
	}

	public UUID entity;
	public boolean tempered;
	public boolean hasAether;
	public float sulfurRatio;
	public float qsilverRatio;

	public SendFlameInfoToClient(UUID entity, boolean tempered, boolean hasAether, float sulfurRatio, float qsilverRatio){
		this.entity = entity;
		this.tempered = tempered;
		this.hasAether = hasAether;
		this.sulfurRatio = sulfurRatio;
		this.qsilverRatio = qsilverRatio;
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
				processMessage(worldClient, entity, tempered, hasAether, sulfurRatio, qsilverRatio);
			}
		});

		return null;
	}

	public void processMessage(WorldClient worldClient, UUID entity, boolean tempered, boolean hasAether, float sulfurRatio, float qsilverRatio){
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
		
		if(ent instanceof EntityFlame){
			EntityFlame entF = (EntityFlame) ent;
			entF.temperedFlame = tempered;
			entF.hasAether = hasAether;
			entF.sulfurRatio = sulfurRatio;
			entF.qsilvrRatio = qsilverRatio;
		}
	}
}

package com.Da_Technomancer.crossroads.API.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendLightningToClient extends Message<SendLightningToClient>{

	public SendLightningToClient(){
		
	}
	
	public BlockPos pos;

	public SendLightningToClient(BlockPos pos){
		this.pos = pos;
	}

	@Override
	public IMessage handleMessage(MessageContext context){
		if(context.side != Side.CLIENT){
			System.err.println("MessageToClient received on wrong side:" + context.side);
			return null;
		}

		Minecraft minecraft = Minecraft.getMinecraft();
		World client = minecraft.world;
		minecraft.addScheduledTask(new Runnable(){
			@Override
			public void run(){
				summonLightning(client, pos);
			}
		});

		return null;
	}
	
	public static void summonLightning(World client, BlockPos pos){
		client.spawnEntity(new EntityLightningBolt(client, pos.getX(), pos.getY(), pos.getZ(), true));
	}
}

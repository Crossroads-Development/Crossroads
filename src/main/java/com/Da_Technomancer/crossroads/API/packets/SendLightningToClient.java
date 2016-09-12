package com.Da_Technomancer.crossroads.API.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.math.BlockPos;
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
		final WorldClient client = (WorldClient) minecraft.theWorld;
		minecraft.addScheduledTask(new Runnable(){
			public void run(){
				processMessage(client, pos);
			}
		});

		return null;
	}

	public void processMessage(WorldClient client, BlockPos pos){
		client.spawnEntityInWorld(new EntityLightningBolt(client, pos.getX(), pos.getY(), pos.getZ(), true));
	}
}

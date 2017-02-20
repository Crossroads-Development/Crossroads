package com.Da_Technomancer.crossroads.API.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendBoolToClient extends Message<SendBoolToClient>{

	public SendBoolToClient(){
	}

	public String sContext;
	public boolean message;
	public BlockPos pos;

	public SendBoolToClient(String context, boolean message, BlockPos pos){
		this.sContext = context;
		this.message = message;
		this.pos = pos;
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
				processMessage(worldClient, sContext, message, pos);
			}
		});

		return null;
	}

	public void processMessage(WorldClient worldClient, String context, boolean message, BlockPos pos){
		if(worldClient == null){
			return;
		}
		TileEntity te = worldClient.getTileEntity(pos);

		if(te instanceof IBoolReceiver){
			((IBoolReceiver) te).receiveBool(context, message);
		}
	}
}

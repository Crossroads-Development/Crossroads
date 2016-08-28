package com.Da_Technomancer.crossroads.API.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendIntToClient extends Message<SendIntToClient>{

	public SendIntToClient(){
	}

	public String sContext;
	public int message;
	public BlockPos pos;

	public SendIntToClient(String context, int message, BlockPos pos){
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
		final WorldClient worldClient = minecraft.theWorld;
		minecraft.addScheduledTask(new Runnable(){
			public void run(){
				processMessage(worldClient, sContext, message, pos);
			}
		});

		return null;
	}

	public void processMessage(WorldClient worldClient, String context, int message, BlockPos pos){
		if(worldClient == null){
			return;
		}
		TileEntity te = worldClient.getTileEntity(pos);

		if(te instanceof IIntReceiver){
			((IIntReceiver) te).receiveInt(context, message);
		}
	}
}

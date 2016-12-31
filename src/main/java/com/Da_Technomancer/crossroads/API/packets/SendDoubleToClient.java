package com.Da_Technomancer.crossroads.API.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendDoubleToClient extends Message<SendDoubleToClient>{

	public SendDoubleToClient(){
		
	}

	public String sContext;
	public double message;
	public BlockPos pos;

	public SendDoubleToClient(String context, double message, BlockPos pos){
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
			@Override
			public void run(){
				processMessage(worldClient, sContext, message, pos);
			}
		});

		return null;
	}

	public void processMessage(WorldClient worldClient, String context, double message, BlockPos pos){
		TileEntity te = worldClient.getTileEntity(pos);

		if(te instanceof IDoubleReceiver){
			((IDoubleReceiver) te).receiveDouble(context, message);
		}
	}
}

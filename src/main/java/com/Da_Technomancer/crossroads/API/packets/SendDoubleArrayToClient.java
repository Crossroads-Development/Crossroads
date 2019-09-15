package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.essentials.packets.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendDoubleArrayToClient extends Message<SendDoubleArrayToClient>{

	public SendDoubleArrayToClient(){
		
	}

	public String sContext;
	public double[] message;
	public BlockPos pos;

	public SendDoubleArrayToClient(String context, double[] message, BlockPos pos){
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

		Minecraft minecraft = Minecraft.getInstance();
		final ClientWorld worldClient = minecraft.world;
		minecraft.addScheduledTask(new Runnable(){
			@Override
			public void run(){
				processMessage(worldClient, sContext, message, pos);
			}
		});

		return null;
	}

	public void processMessage(ClientWorld worldClient, String context, double[] message, BlockPos pos){
		TileEntity te = worldClient.getTileEntity(pos);

		if(te instanceof IDoubleArrayReceiver){
			((IDoubleArrayReceiver) te).receiveDoubles(context, message, null);
		}
	}
}

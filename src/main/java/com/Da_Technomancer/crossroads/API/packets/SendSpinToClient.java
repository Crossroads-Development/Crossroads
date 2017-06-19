package com.Da_Technomancer.crossroads.API.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendSpinToClient extends Message<SendSpinToClient>{

	public SendSpinToClient(){

	}

	public int identifier;
	public float clientW;
	public float angle;
	public BlockPos pos;

	public SendSpinToClient(int identifier, float clientW, float angle, BlockPos pos){
		this.identifier = identifier;
		this.clientW = clientW;
		this.angle = angle;
		this.pos = pos;
	}

	@Override
	public IMessage handleMessage(MessageContext context){
		if(context.side != Side.CLIENT){
			System.err.println("MessageToClient received on wrong side:" + context.side);
			return null;
		}

		Minecraft minecraft = Minecraft.getMinecraft();
		WorldClient worldClient = minecraft.world;
		minecraft.addScheduledTask(new Runnable(){
			@Override
			public void run(){
				TileEntity te = worldClient.getTileEntity(pos);
				if(te instanceof ISpinReceiver){
					((ISpinReceiver) te).receiveSpin(identifier, clientW, angle);
				}
			}
		});

		return null;
	}
}

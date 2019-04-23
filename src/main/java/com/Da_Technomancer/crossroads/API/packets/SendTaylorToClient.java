package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.essentials.packets.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Sends a Taylor series to the client. Used by Master Axes to reduce packet overhead
 */
public class SendTaylorToClient extends Message<SendTaylorToClient>{

	public SendTaylorToClient(){
	}

	public long timestamp;
	public float term0;
	public float term1;
	public float term2;
	public float term3;
	public BlockPos pos;

	public SendTaylorToClient(long timestamp, float[] terms, BlockPos pos){
		this.timestamp = timestamp;
		term0 = terms[0];
		term1 = terms[1];
		term2 = terms[2];
		term3 = terms[3];
		this.pos = pos;
	}

	@Override
	public IMessage handleMessage(MessageContext context){
		if(context.side != Side.CLIENT){
			Main.logger.error("MessageToClient received on wrong side:" + context.side);
			return null;
		}

		Minecraft minecraft = Minecraft.getMinecraft();
		final WorldClient worldClient = minecraft.world;
		minecraft.addScheduledTask(new Runnable(){
			public void run(){
				if(worldClient == null){
					return;
				}
				TileEntity te = worldClient.getTileEntity(pos);

				if(te instanceof ITaylorReceiver){
					((ITaylorReceiver) te).receiveSeries(timestamp, new float[] {term0, term1, term2, term3});
				}
			}
		});

		return null;
	}
}

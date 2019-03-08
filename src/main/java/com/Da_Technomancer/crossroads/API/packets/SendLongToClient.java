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

public class SendLongToClient extends Message<SendLongToClient>{

	public SendLongToClient(){
	}

	public byte identifier;
	public long message;
	public BlockPos pos;

	public SendLongToClient(byte identifier, long message, BlockPos pos){
		this.identifier = identifier;
		this.message = message;
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
			@Override
			public void run(){
				processMessage(worldClient, identifier, message, pos);
			}
		});

		return null;
	}

	public void processMessage(WorldClient worldClient, byte identifier, long message, BlockPos pos){
		if(worldClient == null){
			return;
		}
		TileEntity te = worldClient.getTileEntity(pos);

		if(te instanceof ILongReceiver){
			((ILongReceiver) te).receiveLong(identifier, message, null);
		}
	}
}

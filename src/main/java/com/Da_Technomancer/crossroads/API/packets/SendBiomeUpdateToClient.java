package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendBiomeUpdateToClient extends Message<SendBiomeUpdateToClient>{

	public SendBiomeUpdateToClient(){

	}

	public BlockPos pos;
	public byte newBiome;

	/**
	 * When a biome is changed on the server side, the change isn't sent to clients (visible in f3 menu) until the client dimension switches/rejoins. This packet forces the client to recognize a new biome.
	 * @param pos
	 * @param newBiome
	 */
	public SendBiomeUpdateToClient(BlockPos pos, byte newBiome){
		this.pos = pos;
		this.newBiome = newBiome;
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
				processMessage(worldClient, pos, newBiome);
			}
		});

		return null;
	}

	public void processMessage(WorldClient worldClient, BlockPos pos, byte newBiome){
		worldClient.getChunkFromBlockCoords(pos).getBiomeArray()[(pos.getZ() & 15) << 4 | (pos.getX() & 15)] = newBiome;
	}
}

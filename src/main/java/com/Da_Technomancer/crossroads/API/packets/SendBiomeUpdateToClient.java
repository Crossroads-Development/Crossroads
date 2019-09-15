package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.packets.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
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
	 * When a biome is changed on the server side, the change isn't sent to clients (visible in f3 menu) until the render dimension switches/rejoins. This packet forces the render to recognize a new biome.
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
			Crossroads.logger.error("MessageToClient received on wrong side:" + context.side);
			return null;
		}

		Minecraft minecraft = Minecraft.getInstance();
		final ClientWorld worldClient = minecraft.world;
		minecraft.addScheduledTask(new Runnable(){
			@Override
			public void run(){
				processMessage(worldClient, pos, newBiome);
			}
		});

		return null;
	}

	public void processMessage(ClientWorld worldClient, BlockPos pos, byte newBiome){
		worldClient.getChunk(pos).getBiomeArray()[(pos.getZ() & 15) << 4 | (pos.getX() & 15)] = newBiome;
	}
}

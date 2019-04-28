package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.essentials.packets.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Sends a Taylor series to the client. Used by Master Axes to reduce packet overhead
 */
public class SendMasterKeyToClient extends Message<SendMasterKeyToClient>{

	public SendMasterKeyToClient(){
	}

	public int newKey;

	public SendMasterKeyToClient(int newMasterKey){
		newKey = newMasterKey;
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
				RotaryUtil.setMasterKey(newKey);
			}
		});

		return null;
	}
}

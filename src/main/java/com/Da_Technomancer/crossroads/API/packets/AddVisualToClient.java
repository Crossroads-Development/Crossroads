package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.render.RenderUtil;
import com.Da_Technomancer.essentials.packets.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class AddVisualToClient extends Message<AddVisualToClient>{

	public AddVisualToClient(){
		
	}
	
	public CompoundNBT nbt;

	public AddVisualToClient(CompoundNBT nbt){
		this.nbt = nbt;
	}

	@Override
	public IMessage handleMessage(MessageContext context){
		if(context.side != Side.CLIENT){
			Crossroads.logger.error("MessageToClient received on wrong side:" + context.side);
			return null;
		}

		Minecraft minecraft = Minecraft.getInstance();
		minecraft.addScheduledTask(new Runnable(){
			@Override
			public void run(){
				SafeCallable.effectsToRender.add(RenderUtil.visualFactories[nbt.getInteger("id")].apply(nbt));
			}
		});

		return null;
	}
}

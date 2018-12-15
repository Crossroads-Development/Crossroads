package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.render.RenderUtil;
import com.Da_Technomancer.essentials.packets.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class AddVisualToClient extends Message<AddVisualToClient>{

	public AddVisualToClient(){
		
	}
	
	public NBTTagCompound nbt;

	public AddVisualToClient(NBTTagCompound nbt){
		this.nbt = nbt;
	}

	@Override
	public IMessage handleMessage(MessageContext context){
		if(context.side != Side.CLIENT){
			Main.logger.error("MessageToClient received on wrong side:" + context.side);
			return null;
		}

		Minecraft minecraft = Minecraft.getMinecraft();
		minecraft.addScheduledTask(new Runnable(){
			@Override
			public void run(){
				SafeCallable.effectsToRender.add(RenderUtil.visualFactories[nbt.getInteger("id")].apply(nbt));
			}
		});

		return null;
	}
}

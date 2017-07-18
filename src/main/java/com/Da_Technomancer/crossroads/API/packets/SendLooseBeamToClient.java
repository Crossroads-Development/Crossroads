package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.API.technomancy.LooseBeamRenderable;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendLooseBeamToClient extends Message<SendLooseBeamToClient>{

	public SendLooseBeamToClient(){
		
	}
	
	public NBTTagCompound nbt;

	/**
	 * @param nbt Should represent a {@link LooseBeamRenderable}
	 */
	public SendLooseBeamToClient(NBTTagCompound nbt){
		this.nbt = nbt; 
	}

	@Override
	public IMessage handleMessage(MessageContext context){
		if(context.side != Side.CLIENT){
			System.err.println("MessageToClient received on wrong side:" + context.side);
			return null;
		}

		Minecraft minecraft = Minecraft.getMinecraft();
		minecraft.addScheduledTask(new Runnable(){
			@Override
			public void run(){
				SafeCallable.beamsToRender.add(LooseBeamRenderable.readFromNBT(nbt));
			}
		});

		return null;
	}
}

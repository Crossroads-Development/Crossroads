package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.API.alchemy.LooseArcRenderable;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendLooseArcToClient extends Message<SendLooseArcToClient>{

	public SendLooseArcToClient(){
		
	}
	
	public NBTTagCompound nbt;

	/**
	 * @param nbt Should represent a {@link LooseArcRenderable}
	 */
	public SendLooseArcToClient(NBTTagCompound nbt){
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
				SafeCallable.arcsToRender.add(LooseArcRenderable.readFromNBT(nbt));
			}
		});

		return null;
	}
}

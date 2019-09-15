package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.dimensions.ModDimensions;

import com.Da_Technomancer.essentials.packets.Message;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendDimLoadToClient extends Message<SendDimLoadToClient>{

	public SendDimLoadToClient(){
	}

	public int[] dims;
	public boolean wipePrevious = false;

	public SendDimLoadToClient(int[] dims){
		this.dims = dims;
	}
	
	public SendDimLoadToClient(Integer[] dims){
		this.dims = new int[dims.length];
		for(int i = 0; i < dims.length; i++){
			this.dims[i] = dims[i];
		}
	}

	public SendDimLoadToClient(int[] dims, boolean wipePrevious){
		this.dims = dims;
		this.wipePrevious = wipePrevious;
	}

	@Override
	public IMessage handleMessage(MessageContext context){
		if(context != null && context.side != Side.CLIENT){
			System.err.println("MessageToClient received on wrong side:" + context.side);
			return null;
		}

		Minecraft minecraft = Minecraft.getInstance();
		minecraft.addScheduledTask(new Runnable(){
			public void run(){
				if(wipePrevious){
					for(int i : DimensionManager.getDimensions(ModDimensions.workspaceDimType)){
						DimensionManager.unregisterDimension(i);
					}
				}
				for(int i : dims){
					if(!DimensionManager.isDimensionRegistered(i)){
						DimensionManager.registerDimension(i, ModDimensions.workspaceDimType);
					}
				}
			}
		});

		return null;
	}
}

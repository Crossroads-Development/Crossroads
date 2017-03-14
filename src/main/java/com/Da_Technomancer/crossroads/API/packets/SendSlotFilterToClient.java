package com.Da_Technomancer.crossroads.API.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendSlotFilterToClient extends Message<SendSlotFilterToClient>{

	public SendSlotFilterToClient(){
		
	}

	public NBTTagCompound nbt;
	public BlockPos pos;

	public SendSlotFilterToClient(NBTTagCompound nbt, BlockPos pos){
		this.nbt = nbt;
		this.pos = pos;
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
				SafeCallable.chestLock(minecraft.world, nbt, pos);
			}
		});

		return null;
	}
}

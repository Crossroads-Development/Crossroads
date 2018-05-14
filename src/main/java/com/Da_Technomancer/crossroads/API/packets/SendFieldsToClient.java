package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.API.technomancy.ChunkField;
import com.Da_Technomancer.crossroads.API.technomancy.FieldWorldSavedData;
import com.Da_Technomancer.essentials.packets.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendFieldsToClient extends Message<SendFieldsToClient>{

	public SendFieldsToClient(){

	}

	public NBTTagCompound fieldNBT;
	public long chunk;

	public SendFieldsToClient(ChunkField field, long chunk){
		this.fieldNBT = new NBTTagCompound();
		if(field == null){
			fieldNBT.setBoolean("dead", true);
		}else{
			field.writeToNBT(fieldNBT, chunk);
		}
		this.chunk = chunk;
	}

	@Override
	public IMessage handleMessage(MessageContext context){
		if(context.side != Side.CLIENT){
			System.err.println("MessageToClient received on wrong side:" + context.side);
			return null;
		}

		Minecraft minecraft = Minecraft.getMinecraft();
		minecraft.addScheduledTask(new Runnable(){
			public void run(){
				processMessage(fieldNBT.getBoolean("dead") ? null : ChunkField.readFromNBT(fieldNBT, chunk), chunk, minecraft.world);
			}
		});

		return null;
	}

	public void processMessage(ChunkField field, long chunk, World world){
		if(world == null){
			return;
		}
		FieldWorldSavedData saved = FieldWorldSavedData.get(world);
		if(field == null){
			saved.fieldNodes.remove(chunk);
		}else{
			saved.fieldNodes.put(chunk, field);
		}
	}
}

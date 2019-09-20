package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.essentials.packets.ClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class SendDoubleArrayToClient extends ClientPacket{

	public byte id;
	public double[] message;
	public BlockPos pos;

	private static final Field[] FIELDS = fetchFields(SendDoubleArrayToClient.class, "id", "message", "pos");

	@SuppressWarnings("unused")
	public SendDoubleArrayToClient(){

	}

	public SendDoubleArrayToClient(byte context, double[] message, BlockPos pos){
		this.id = context;
		this.message = message;
		this.pos = pos;
	}

	@Nonnull
	@Override
	protected Field[] getFields(){
		return FIELDS;
	}

	@Override
	protected void run(){
		World worldClient = Minecraft.getInstance().world;
		TileEntity te = worldClient.getTileEntity(pos);

		if(te instanceof IDoubleArrayReceiver){
			((IDoubleArrayReceiver) te).receiveDoubles(id, message, null);
		}
	}
}

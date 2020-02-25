package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.essentials.packets.ClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class SendStringToClient extends ClientPacket{

	public byte context;
	public String message;
	public BlockPos pos;

	private static final Field[] FIELDS = fetchFields(SendStringToClient.class, "context", "message", "pos");

	@SuppressWarnings("unused")
	public SendStringToClient(){

	}

	public SendStringToClient(int context, String message, BlockPos pos){
		this((byte) context, message, pos);
	}

	public SendStringToClient(byte context, String message, BlockPos pos){
		this.context = context;
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
		World w = Minecraft.getInstance().world;
		if(w == null){
			return;
		}
		TileEntity te = w.getTileEntity(pos);

		if(te instanceof IStringReceiver){
			((IStringReceiver) te).receiveString(context, message, null);
		}
	}
}

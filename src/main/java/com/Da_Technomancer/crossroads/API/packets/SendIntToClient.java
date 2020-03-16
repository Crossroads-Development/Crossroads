package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.essentials.packets.ClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class SendIntToClient extends ClientPacket{

	public byte identifier;
	public int message;
	public BlockPos pos;

	private static final Field[] FIELDS = fetchFields(SendIntToClient.class, "identifier", "message", "pos");

	@SuppressWarnings("unused")
	public SendIntToClient(){

	}

	public SendIntToClient(byte identifier, int message, BlockPos pos){
		this.identifier = identifier;
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
		TileEntity te = Minecraft.getInstance().world.getTileEntity(pos);

		if(te instanceof IIntReceiver){
			((IIntReceiver) te).receiveInt(identifier, message, null);
		}
	}
}

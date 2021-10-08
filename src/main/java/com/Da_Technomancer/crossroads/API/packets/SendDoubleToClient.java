package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.essentials.packets.ClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class SendDoubleToClient extends ClientPacket{

	public byte identifier;
	public double message;
	public BlockPos pos;

	private static final Field[] FIELDS = fetchFields(SendDoubleToClient.class, "identifier", "message", "pos");

	@SuppressWarnings("unused")
	public SendDoubleToClient(){

	}

	public SendDoubleToClient(byte identifier, double message, BlockPos pos){
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
		BlockEntity te = Minecraft.getInstance().level.getBlockEntity(pos);

		if(te instanceof IDoubleReceiver){
			((IDoubleReceiver) te).receiveDouble(identifier, message, null);
		}
	}
}

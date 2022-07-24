package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.essentials.api.packets.ClientPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class SendIntArrayToClient extends ClientPacket{

	public byte id;
	public int[] message;
	public BlockPos pos;

	private static final Field[] FIELDS = fetchFields(SendIntArrayToClient.class, "id", "message", "pos");

	@SuppressWarnings("unused")
	public SendIntArrayToClient(){

	}

	public SendIntArrayToClient(byte context, int[] message, BlockPos pos){
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
		BlockEntity te = SafeCallable.getClientWorld().getBlockEntity(pos);

		if(te instanceof IIntArrayReceiver){
			((IIntArrayReceiver) te).receiveInts(id, message, null);
		}
	}
}

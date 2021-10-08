package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.essentials.packets.ClientPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

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
		Level world = SafeCallable.getClientWorld();
		if(world == null){
			return;
		}
		BlockEntity te = world.getBlockEntity(pos);

		if(te instanceof IIntReceiver){
			((IIntReceiver) te).receiveInt(identifier, message, null);
		}
	}
}

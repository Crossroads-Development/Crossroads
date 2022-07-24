package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.essentials.api.packets.ServerPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class SendStringToServer extends ServerPacket{

	public byte id;
	public String message;
	public BlockPos pos;
	public int dim;

	private static final Field[] FIELDS = fetchFields(SendStringToServer.class, "id", "message", "pos", "dim");

	@SuppressWarnings("unused")
	public SendStringToServer(){

	}

	public SendStringToServer(byte context, String message, BlockPos pos, int dim){
		this.id = context;
		this.message = message;
		this.pos = pos;
		this.dim = dim;
	}

	@Nonnull
	@Override
	protected Field[] getFields(){
		return FIELDS;
	}

	@Override
	protected void run(@Nullable ServerPlayer serverPlayerEntity){
		if(serverPlayerEntity != null && serverPlayerEntity.level != null){
			BlockEntity te = serverPlayerEntity.level.getBlockEntity(pos);

			if(te instanceof IStringReceiver){
				((IStringReceiver) te).receiveString(id, message, serverPlayerEntity);
			}
		}
	}
}

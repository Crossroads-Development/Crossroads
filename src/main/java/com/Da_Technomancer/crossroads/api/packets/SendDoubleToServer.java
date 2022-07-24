package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.essentials.api.packets.ServerPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class SendDoubleToServer extends ServerPacket{

	public byte context;
	public double message;
	public BlockPos pos;
	public int dim;

	private static final Field[] FIELDS = fetchFields(SendDoubleToServer.class, "context", "message", "pos", "dim");

	@SuppressWarnings("unused")
	public SendDoubleToServer(){

	}

	public SendDoubleToServer(byte context, double message, BlockPos pos, int dim){
		this.context = context;
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
		if(serverPlayerEntity != null){
			BlockEntity te = serverPlayerEntity.level.getBlockEntity(pos);

			if(te instanceof IDoubleReceiver){
				((IDoubleReceiver) te).receiveDouble(context, message, serverPlayerEntity);
			}
		}
	}
}

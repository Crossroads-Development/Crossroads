package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.essentials.packets.ServerPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

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
	protected void run(@Nullable ServerPlayerEntity serverPlayerEntity){
		if(serverPlayerEntity != null){
			TileEntity te = serverPlayerEntity.world.getTileEntity(pos);

			if(te instanceof IDoubleReceiver){
				((IDoubleReceiver) te).receiveDouble(context, message, serverPlayerEntity);
			}
		}
	}
}

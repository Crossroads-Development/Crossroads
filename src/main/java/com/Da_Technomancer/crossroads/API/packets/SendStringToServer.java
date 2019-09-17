package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.essentials.packets.ServerPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

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
	protected void run(@Nullable ServerPlayerEntity serverPlayerEntity){
		if(serverPlayerEntity != null && serverPlayerEntity.world != null){
			TileEntity te = serverPlayerEntity.world.getTileEntity(pos);

			if(te instanceof IStringReceiver){
				((IStringReceiver) te).receiveString(id, message, serverPlayerEntity);
			}
		}
	}
}

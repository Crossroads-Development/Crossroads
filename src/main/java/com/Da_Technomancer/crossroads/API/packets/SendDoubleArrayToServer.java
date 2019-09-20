package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.essentials.packets.ServerPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class SendDoubleArrayToServer extends ServerPacket{

	public byte id;
	public double message[];
	public BlockPos pos;
	public int dim;

	private static final Field[] FIELDS = fetchFields(SendDoubleArrayToServer.class, "id", "message", "pos", "dim");

	@SuppressWarnings("unused")
	public SendDoubleArrayToServer(){

	}

	public SendDoubleArrayToServer(byte context, double[] message, BlockPos pos, int dim){
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
		if(serverPlayerEntity != null){
			World world = serverPlayerEntity.world;
			TileEntity te = world.getTileEntity(pos);

			if(te instanceof IDoubleArrayReceiver){
				((IDoubleArrayReceiver) te).receiveDoubles(id, message, serverPlayerEntity);
			}
		}
	}
}

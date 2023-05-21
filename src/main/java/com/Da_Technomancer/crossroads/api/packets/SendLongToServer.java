package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.essentials.api.packets.ILongReceiver;
import com.Da_Technomancer.essentials.api.packets.ServerPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class SendLongToServer extends ServerPacket{

	//TODO move to Essentials

	public BlockPos pos;
	public byte id;
	public long val;

	private static final Field[] FIELDS = fetchFields(SendLongToServer.class, "pos", "id", "val");

	public SendLongToServer(){
	}

	public SendLongToServer(int id, long val, BlockPos pos){
		this((byte)id, val, pos);
	}

	public SendLongToServer(byte id, long val, BlockPos pos){
		this.id = id;
		this.val = val;
		this.pos = pos;
	}

	@Override
	@Nonnull
	protected Field[] getFields(){
		return FIELDS;
	}

	@Override
	protected void run(@Nullable ServerPlayer player){
		if(player != null){
			BlockEntity te = player.getCommandSenderWorld().getBlockEntity(this.pos);
			if(te instanceof ILongReceiver lte){
				lte.receiveLong(this.id, this.val, player);
			}
		}
	}
}

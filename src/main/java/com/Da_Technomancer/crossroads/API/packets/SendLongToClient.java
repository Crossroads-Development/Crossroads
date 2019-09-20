package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.essentials.packets.ClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class SendLongToClient extends ClientPacket{

	public byte identifier;
	public long message;
	public BlockPos pos;

	private static final Field[] FIELDS = fetchFields(SendLongToClient.class, "identifier", "message", "pos");

	@SuppressWarnings("unused")
	public SendLongToClient(){

	}

	public SendLongToClient(byte identifier, long message, BlockPos pos){
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
		World worldClient = Minecraft.getInstance().world;
		if(worldClient == null){
			return;
		}
		TileEntity te = worldClient.getTileEntity(pos);

		if(te instanceof ILongReceiver){
			((ILongReceiver) te).receiveLong(identifier, message, null);
		}
	}
}

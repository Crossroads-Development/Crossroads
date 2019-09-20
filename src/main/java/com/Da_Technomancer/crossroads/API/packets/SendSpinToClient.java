package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.essentials.packets.ClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class SendSpinToClient extends ClientPacket{

	public byte identifier;
	public float clientW;
	public float angle;
	public BlockPos pos;

	private static final Field[] FIELDS = fetchFields(SendSpinToClient.class, "identifier", "clientW", "angle", "pos");

	@SuppressWarnings("unused")
	public SendSpinToClient(){

	}

	public SendSpinToClient(byte identifier, float clientW, float angle, BlockPos pos){
		this.identifier = identifier;
		this.clientW = clientW;
		this.angle = angle;
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
		TileEntity te = worldClient.getTileEntity(pos);
		if(te instanceof ISpinReceiver){
			((ISpinReceiver) te).receiveSpin(identifier, clientW, angle);
		}
	}
}

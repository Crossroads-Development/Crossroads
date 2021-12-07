package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.essentials.packets.ClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

/**
 * Sends a Taylor series to the client. Used by Master Axes to reduce packet overhead
 */
public class SendTaylorToClient extends ClientPacket{

	public long timestamp;
	public float term0;
	public float term1;
	public float term2;
	public float term3;
	public BlockPos pos;

	private static final Field[] FIELDS = fetchFields(SendTaylorToClient.class, "timestamp", "term0", "term1", "term2", "term3", "pos");

	@SuppressWarnings("unused")
	public SendTaylorToClient(){

	}

	public SendTaylorToClient(long timestamp, float[] terms, BlockPos pos){
		this.timestamp = timestamp;
		term0 = terms[0];
		term1 = terms[1];
		term2 = terms[2];
		term3 = terms[3];
		this.pos = pos;
	}

	@Nonnull
	@Override
	protected Field[] getFields(){
		return FIELDS;
	}

	@Override
	protected void run(){
		BlockEntity te = Minecraft.getInstance().level.getBlockEntity(pos);
		if(te instanceof ITaylorReceiver){
			((ITaylorReceiver) te).receiveSeries(timestamp, new float[] {term0, term1, term2, term3});
		}
	}
}

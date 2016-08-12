package com.Da_Technomancer.crossroads.API.packets;

import net.minecraft.util.math.BlockPos;

public interface IPosReceiver{

	public void receivePos(String context, BlockPos message);
	
}
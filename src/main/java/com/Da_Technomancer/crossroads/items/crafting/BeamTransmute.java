package com.Da_Technomancer.crossroads.items.crafting;

import net.minecraft.block.BlockState;

public class BeamTransmute{
	
	public final BlockState state;
	public final int minPower;
	
	public BeamTransmute(BlockState state, int minPower){
		this.state = state;
		this.minPower = minPower;
	}
}

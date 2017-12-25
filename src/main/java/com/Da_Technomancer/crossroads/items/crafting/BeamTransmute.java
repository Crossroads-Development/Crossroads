package com.Da_Technomancer.crossroads.items.crafting;

import net.minecraft.block.state.IBlockState;

public class BeamTransmute{
	
	public final IBlockState state;
	public final int minPower;
	
	public BeamTransmute(IBlockState state, int minPower){
		this.state = state;
		this.minPower = minPower;
	}
}

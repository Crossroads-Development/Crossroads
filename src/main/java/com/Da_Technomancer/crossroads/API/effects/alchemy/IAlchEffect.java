package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.MatterPhase;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IAlchEffect{
	
	public void doEffect(World world, BlockPos pos, double amount, MatterPhase phase);

}

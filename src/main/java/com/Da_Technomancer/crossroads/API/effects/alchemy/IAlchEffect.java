package com.Da_Technomancer.crossroads.API.effects.alchemy;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IAlchEffect{
	
	public void doEffect(World world, BlockPos pos, double amount, double temp, EnumMatterPhase phase);
	
	/**
	 * @param world
	 * @param pos
	 * @param amount
	 * @param phase
	 * @param contents The full contents of the caller. Null if not applicable. 
	 */
	public default void doEffectAdv(World world, BlockPos pos, double amount, double temp, EnumMatterPhase phase, @Nullable ReagentStack[] contents){
		doEffect(world, pos, amount, temp, phase);
	}
}

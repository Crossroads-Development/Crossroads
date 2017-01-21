package com.Da_Technomancer.crossroads.API.effects;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IEffect{

	/**
	 * 
	 * @param worldIn
	 * @param pos
	 * @param mult There is no requirement for an IEffect to use the mult value, but if used then it multiplies the power of the effect.
	 * 
	 * This should only ever be called on the server side.
	 */
	public void doEffect(World worldIn, BlockPos pos, double mult);

}

package com.Da_Technomancer.crossroads.API.effects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IEffect{

	/**
	 * @param worldIn The world of the effect
	 * @param pos The position of the effect
	 *
	 * This should only ever be called on the server side.
	 */
	void doEffect(Level worldIn, BlockPos pos);

}

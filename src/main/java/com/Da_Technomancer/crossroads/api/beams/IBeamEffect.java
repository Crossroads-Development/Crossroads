package com.Da_Technomancer.crossroads.api.beams;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IBeamEffect{

	/**
	 * @param worldIn The world of the effect
	 * @param pos The position of the effect
	 *
	 * This should only ever be called on the server side.
	 */
	void doEffect(Level worldIn, BlockPos pos);

}

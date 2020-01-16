package com.Da_Technomancer.crossroads.API.effects;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IEffect{

	/**
	 * @param worldIn The world of the effect
	 * @param pos The position of the effect
	 *
	 * This should only ever be called on the server side.
	 */
	void doEffect(World worldIn, BlockPos pos);

}

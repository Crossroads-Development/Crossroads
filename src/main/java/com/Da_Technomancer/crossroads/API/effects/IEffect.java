package com.Da_Technomancer.crossroads.API.effects;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IEffect{

	/**
	 * @param worldIn The world of the effect
	 * @param pos The position of the effect
	 * @param mult There is no requirement for an IEffect to use the mult value, but if used then it multiplies the power of the effect. 
	 * @param dir The direction this is being externally triggered from. May be null if this is internal
	 *
	 * This should only ever be called on the server side.
	 */
	public void doEffect(World worldIn, BlockPos pos, int mult, @Nullable EnumFacing dir);

}

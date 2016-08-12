package com.Da_Technomancer.crossroads.API.heat.overheatEffects;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BurnEffect implements OverheatEffect{

	@Override
	public void onOverheat(World worldIn, BlockPos pos) {
		worldIn.setBlockState(pos, Blocks.FIRE.getDefaultState(), 3);
	}
}

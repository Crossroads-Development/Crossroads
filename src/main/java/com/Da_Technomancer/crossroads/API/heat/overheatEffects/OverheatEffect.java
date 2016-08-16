package com.Da_Technomancer.crossroads.API.heat.overheatEffects;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface OverheatEffect{

	public void onOverheat(World worldIn, BlockPos pos);

}

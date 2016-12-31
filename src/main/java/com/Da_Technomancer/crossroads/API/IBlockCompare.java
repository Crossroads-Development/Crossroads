package com.Da_Technomancer.crossroads.API;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBlockCompare{
	
	public double getOutput(World worldIn, BlockPos pos);

}

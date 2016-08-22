package com.Da_Technomancer.crossroads.API.heat.overheatEffects;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockEffect implements OverheatEffect{

	private final IBlockState block;
	
	public BlockEffect(IBlockState block){
		this.block = block;
	}
	
	@Override
	public void onOverheat(World worldIn, BlockPos pos){
		worldIn.setBlockState(pos, block, 3);
	}

}

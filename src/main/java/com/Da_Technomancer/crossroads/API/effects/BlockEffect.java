package com.Da_Technomancer.crossroads.API.effects;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockEffect implements IEffect{

	private final IBlockState block;
	
	public BlockEffect(IBlockState block){
		this.block = block;
	}
	
	@Override
	public void doEffect(World worldIn, BlockPos pos){
		worldIn.setBlockState(pos, block, 3);
	}

}

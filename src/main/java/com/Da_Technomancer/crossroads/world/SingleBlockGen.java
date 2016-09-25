package com.Da_Technomancer.crossroads.world;

import java.util.Random;

import com.google.common.base.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class SingleBlockGen extends WorldGenerator{

	private final IBlockState block;
	private final Predicate<IBlockState> target;

	protected SingleBlockGen(IBlockState block, Predicate<IBlockState> target){
		this.block = block;
		this.target = target;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos pos){
		if(world.getBlockState(pos).getBlock().isReplaceableOreGen(world.getBlockState(pos), world, pos, this.target))
			world.setBlockState(pos, this.block);
		return true;
	}
}

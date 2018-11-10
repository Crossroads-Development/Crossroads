package com.Da_Technomancer.crossroads.world;

import com.google.common.base.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class GemGen extends WorldGenerator{

	private final IBlockState created;
	private final Predicate<IBlockState> target;

	public GemGen(IBlockState spawned, Predicate<IBlockState> stone){
		created = spawned;
		target = stone;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos pos){
		IBlockState state = world.getBlockState(pos);
		if(state.getBlock().isReplaceableOreGen(state, world, pos, target)){
			world.setBlockState(pos, created);
		}
		return true;
	}
}

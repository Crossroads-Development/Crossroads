package com.Da_Technomancer.crossroads.world;

import com.google.common.base.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;

public class GemGen extends Feature{

	private final BlockState created;
	private final Predicate<BlockState> target;

	public GemGen(BlockState spawned, Predicate<BlockState> stone){
		created = spawned;
		target = stone;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos pos){
		BlockState state = world.getBlockState(pos);
		if(state.getBlock().isReplaceableOreGen(state, world, pos, target)){
			world.setBlockState(pos, created);
		}
		return true;
	}
}

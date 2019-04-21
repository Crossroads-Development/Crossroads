package com.Da_Technomancer.crossroads.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;

import java.util.Random;

/**
 * Uses the same logic as the vanilla WorldGenMinable, but offsets world gen calls by +16, 0, +16 as opposed to the +8, 0, +8 of the default WorldGenMinable class
 * It does this to further reduce the chance of cascading world gen lag by reducing the chance of large ore veins extending into ungenerated chunks.
 */
public class LargeOreGen extends WorldGenMinable{

	public LargeOreGen(IBlockState state, int blockCount){
		super(state, blockCount);
	}

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos position){
		return super.generate(worldIn, rand, position.add(8, 0, 8));
	}
}

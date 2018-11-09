package com.Da_Technomancer.crossroads.world;

import java.util.Random;

import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import com.google.common.base.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class RubyGen extends WorldGenerator{

	private static final IBlockState created = OreSetup.oreRuby.getDefaultState();
	private static final Predicate<IBlockState> target = BlockMatcher.forBlock(Blocks.QUARTZ_ORE);

	@Override
	public boolean generate(World world, Random random, BlockPos pos){
		IBlockState state = world.getBlockState(pos);
		if(state.getBlock().isReplaceableOreGen(state, world, pos, target)){
			world.setBlockState(pos, created);
		}
		return true;
	}
}

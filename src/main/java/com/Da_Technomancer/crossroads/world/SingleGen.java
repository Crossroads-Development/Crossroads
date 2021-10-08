package com.Da_Technomancer.crossroads.world;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

import java.util.Random;

public class SingleGen extends Feature<OreConfiguration>{

	protected SingleGen(){
		super(OreConfiguration.CODEC);
	}

	@Override
	public boolean place(WorldGenLevel world, ChunkGenerator generator, Random rand, BlockPos pos, OreConfiguration config){
		BlockState state = world.getBlockState(pos);
		if(config.target.test(state, rand)){
			world.setBlock(pos, config.state, 2);
			return true;
		}
		return false;
	}
}

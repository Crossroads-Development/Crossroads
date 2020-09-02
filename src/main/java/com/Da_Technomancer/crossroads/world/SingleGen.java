package com.Da_Technomancer.crossroads.world;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import java.util.Random;

public class SingleGen extends Feature<OreFeatureConfig>{

	protected SingleGen(){
		super(OreFeatureConfig.field_236566_a_);
	}

	@Override
	public boolean func_241855_a(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, OreFeatureConfig config){
		BlockState state = world.getBlockState(pos);
		if(config.target.test(state, rand)){
			world.setBlockState(pos, config.state, 2);
			return true;
		}
		return false;
	}
}

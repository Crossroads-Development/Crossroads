package com.Da_Technomancer.crossroads.world;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

public class SingleGen extends Feature<OreConfiguration>{

	protected SingleGen(){
		super(OreConfiguration.CODEC);
	}

	@Override
	public boolean place(FeaturePlaceContext<OreConfiguration> context){
		BlockState state = context.level().getBlockState(context.origin());
		OreConfiguration config = context.config();
		for(OreConfiguration.TargetBlockState target : config.targetStates){
			if(target.target.test(state, context.random())){
				context.level().setBlock(context.origin(), target.state, 2);
				return true;
			}
		}

		return false;
	}
}

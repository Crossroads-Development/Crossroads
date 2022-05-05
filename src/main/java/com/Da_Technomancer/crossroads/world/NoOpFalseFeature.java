package com.Da_Technomancer.crossroads.world;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.NoOpFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Does nothing, reports that it 'failed' to place (normal NoOpFeature reports that it succeeded at placing)
 */
public class NoOpFalseFeature extends NoOpFeature{

	public NoOpFalseFeature(){
		super(NoneFeatureConfiguration.CODEC);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> config){
		return false;
	}
}

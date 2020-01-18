package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.fluids.CrossroadsFluids;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class LumenEffect extends AetherEffect{

	@Override
	protected Block soilBlock(){
		return Blocks.CLAY;
	}

	@Override
	protected Block rockBlock(){
		return Blocks.PRISMARINE;
	}

	@Override
	protected Block crystalBlock(){
		return Blocks.SEA_LANTERN;
	}

	@Override
	protected Block fluidBlock(){
		return CrossroadsFluids.distilledWater.block;
	}

	@Override
	protected Biome biome(){
		return Biomes.DEEP_OCEAN;
	}
}

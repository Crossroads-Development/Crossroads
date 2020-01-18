package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class StasisolEffect extends AetherEffect{

	@Override
	protected Block soilBlock(){
		return Blocks.SNOW;
	}

	@Override
	protected Block rockBlock(){
		return Blocks.PACKED_ICE;
	}

	@Override
	protected Block crystalBlock(){
		return CRBlocks.blockPureQuartz;
	}

	@Override
	protected Block fluidBlock(){
		return Blocks.ICE;
	}

	@Override
	protected Biome biome(){
		return Biomes.SNOWY_TUNDRA;
	}
}

package com.Da_Technomancer.crossroads.API.effects.alchemy;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class EldrineEffect extends AetherEffect{

	@Override
	protected Block soilBlock(){
		return Blocks.SOUL_SAND;
	}

	@Override
	protected Block rockBlock(){
		return Blocks.NETHERRACK;
	}

	@Override
	protected Block crystalBlock(){
		return Blocks.GLOWSTONE;
	}

	@Override
	protected Block fluidBlock(){
		return Blocks.LAVA;
	}

	@Override
	protected Biome biome(){
		return Biomes.field_235254_j_;//Nether wastes- the 'normal' nether biome pre-1.16
	}
}

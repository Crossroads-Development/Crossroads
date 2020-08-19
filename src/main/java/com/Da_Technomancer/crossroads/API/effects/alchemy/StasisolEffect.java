package com.Da_Technomancer.crossroads.API.effects.alchemy;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.RegistryKey;
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
		return Blocks.BLUE_ICE;
	}

	@Override
	protected Block fluidBlock(){
		return Blocks.ICE;
	}

	@Override
	protected RegistryKey<Biome> biome(){
		return Biomes.SNOWY_TUNDRA;
	}
}

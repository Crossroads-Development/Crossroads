package com.Da_Technomancer.crossroads.API.effects.alchemy;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class StasisolEffect extends AetherEffect{

	@Override
	protected Block soilBlock(){
		return Blocks.SNOW_BLOCK;
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
	protected Block woodBlock(){
		return Blocks.SPRUCE_LOG;
	}

	@Override
	protected Block foliageBlock(){
		return Blocks.SPRUCE_LEAVES;
	}

	@Override
	protected ResourceKey<Biome> biome(){
		return Biomes.SNOWY_PLAINS;
	}

	@Override
	public Component getName(){
		return new TranslatableComponent("effect.terraform_snow");
	}
}

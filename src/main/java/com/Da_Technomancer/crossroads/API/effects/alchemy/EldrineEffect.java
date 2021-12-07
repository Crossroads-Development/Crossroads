package com.Da_Technomancer.crossroads.API.effects.alchemy;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class EldrineEffect extends AetherEffect{

	@Override
	protected Block soilBlock(){
		return Blocks.SOUL_SOIL;
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
	protected Block woodBlock(){
		return Blocks.CRIMSON_STEM;
	}

	@Override
	protected Block foliageBlock(){
		return Blocks.NETHER_WART_BLOCK;
	}

	@Override
	protected ResourceKey<Biome> biome(){
		return Biomes.NETHER_WASTES;
	}

	@Override
	public Component getName(){
		return new TranslatableComponent("effect.terraform_nether");
	}
}

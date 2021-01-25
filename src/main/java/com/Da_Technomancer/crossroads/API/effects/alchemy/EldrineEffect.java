package com.Da_Technomancer.crossroads.API.effects.alchemy;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

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
	protected RegistryKey<Biome> biome(){
		return Biomes.NETHER_WASTES;
	}

	@Override
	public ITextComponent getName(){
		return new TranslationTextComponent("effect.terraform_nether");
	}
}

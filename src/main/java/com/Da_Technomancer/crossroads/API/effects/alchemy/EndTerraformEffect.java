package com.Da_Technomancer.crossroads.API.effects.alchemy;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class EndTerraformEffect extends AetherEffect{

	@Override
	protected Block soilBlock(){
		return Blocks.END_STONE;
	}

	@Override
	protected Block rockBlock(){
		return Blocks.END_STONE;
	}

	@Override
	protected Block crystalBlock(){
		return Blocks.PURPUR_BLOCK;
	}

	@Override
	protected Block fluidBlock(){
		return Blocks.AIR;
	}

	@Override
	protected Block woodBlock(){
		return Blocks.CHORUS_PLANT;
	}

	@Override
	protected Block foliageBlock(){
		return Blocks.AIR;
	}

	@Override
	protected RegistryKey<Biome> biome(){
		return Biomes.END_BARRENS;
	}

	@Override
	public ITextComponent getName(){
		return new TranslationTextComponent("effect.terraform_end");
	}
}

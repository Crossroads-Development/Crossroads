package com.Da_Technomancer.crossroads.API.effects.alchemy;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class JungleTerraformEffect extends AetherEffect{

	@Override
	protected Block woodBlock(){
		return Blocks.JUNGLE_LOG;
	}

	@Override
	protected Block foliageBlock(){
		return Blocks.JUNGLE_LEAVES;
	}

	@Override
	protected RegistryKey<Biome> biome(){
		return Biomes.JUNGLE;
	}

	@Override
	public ITextComponent getName(){
		return new TranslationTextComponent("effect.terraform_jungle");
	}
}

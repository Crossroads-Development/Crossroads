package com.Da_Technomancer.crossroads.API.effects.alchemy;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class FlowerForestTerraformEffect extends AetherEffect{

	@Override
	protected RegistryKey<Biome> biome(){
		return Biomes.FLOWER_FOREST;
	}

	@Override
	protected Block crystalBlock(){
		return Blocks.HONEY_BLOCK;
	}

	@Override
	public ITextComponent getName(){
		return new TranslationTextComponent("effect.terraform_flower_forest");
	}
}

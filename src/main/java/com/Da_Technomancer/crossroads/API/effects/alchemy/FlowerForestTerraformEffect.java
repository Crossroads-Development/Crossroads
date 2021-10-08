package com.Da_Technomancer.crossroads.API.effects.alchemy;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class FlowerForestTerraformEffect extends AetherEffect{

	@Override
	protected ResourceKey<Biome> biome(){
		return Biomes.FLOWER_FOREST;
	}

	@Override
	protected Block crystalBlock(){
		return Blocks.HONEY_BLOCK;
	}

	@Override
	public Component getName(){
		return new TranslatableComponent("effect.terraform_flower_forest");
	}
}

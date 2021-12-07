package com.Da_Technomancer.crossroads.API.effects.alchemy;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

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
	protected ResourceKey<Biome> biome(){
		return Biomes.JUNGLE;
	}

	@Override
	public Component getName(){
		return new TranslatableComponent("effect.terraform_jungle");
	}
}

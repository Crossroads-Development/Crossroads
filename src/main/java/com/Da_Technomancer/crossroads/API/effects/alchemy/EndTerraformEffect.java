package com.Da_Technomancer.crossroads.API.effects.alchemy;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

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
	protected ResourceKey<Biome> biome(){
		return Biomes.END_BARRENS;
	}

	@Override
	public Component getName(){
		return new TranslatableComponent("effect.terraform_end");
	}
}

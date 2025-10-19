package com.Da_Technomancer.crossroads.effects.alchemy_effects;

import com.Da_Technomancer.crossroads.api.alchemy.TerraformEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class JungleTerraformEffect extends TerraformEffect{

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
	protected Block soilBlock(){
		return Blocks.GRASS_BLOCK;
	}

	@Override
	protected Block rockBlock(){
		return Blocks.STONE;
	}

	@Override
	protected Block fluidBlock(){
		return Blocks.WATER;
	}

	@Override
	protected Block crystalBlock(){
		return Blocks.GLASS;
	}

	@Override
	public Component getName(){
		return Component.translatable("effect.terraform_jungle");
	}
}

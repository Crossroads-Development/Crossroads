package com.Da_Technomancer.crossroads.API.effects.alchemy;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class MushroomTerraformEffect extends AetherEffect{

	@Override
	protected Block soilBlock(){
		return Blocks.MYCELIUM;
	}

	@Override
	protected Block crystalBlock(){
		return Blocks.SHROOMLIGHT;
	}

	@Override
	protected Block woodBlock(){
		return Blocks.MUSHROOM_STEM;
	}

	@Override
	protected Block foliageBlock(){
		return Blocks.RED_MUSHROOM_BLOCK;
	}

	@Override
	protected ResourceKey<Biome> biome(){
		return Biomes.MUSHROOM_FIELDS;
	}

	@Override
	public Component getName(){
		return new TranslatableComponent("effect.terraform_mushroom");
	}
}

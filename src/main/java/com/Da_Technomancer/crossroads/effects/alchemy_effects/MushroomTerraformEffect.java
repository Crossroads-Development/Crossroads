package com.Da_Technomancer.crossroads.effects.alchemy_effects;

import com.Da_Technomancer.crossroads.api.alchemy.TerraformEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class MushroomTerraformEffect extends TerraformEffect{

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
	protected Block rockBlock(){
		return Blocks.STONE;
	}

	@Override
	protected Block fluidBlock(){
		return Blocks.WATER;
	}

	@Override
	public Component getName(){
		return Component.translatable("effect.terraform_mushroom");
	}
}

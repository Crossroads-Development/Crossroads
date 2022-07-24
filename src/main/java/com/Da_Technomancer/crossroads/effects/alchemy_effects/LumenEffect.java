package com.Da_Technomancer.crossroads.effects.alchemy_effects;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class LumenEffect extends AetherEffect{

	@Override
	protected Block soilBlock(){
		return Blocks.SAND;
	}

	@Override
	protected Block rockBlock(){
		return Blocks.SANDSTONE;
	}

	@Override
	protected Block crystalBlock(){
		return CRBlocks.blockSalt;
	}

	@Override
	protected Block fluidBlock(){
		return Blocks.AIR;
	}

	@Override
	protected Block woodBlock(){
		return Blocks.BONE_BLOCK;
	}

	@Override
	protected Block foliageBlock(){
		return Blocks.AIR;
	}

	@Override
	protected ResourceKey<Biome> biome(){
		return Biomes.DESERT;
	}

	@Override
	public Component getName(){
		return Component.translatable("effect.terraform_desert");
	}
}

package com.Da_Technomancer.crossroads.API.effects.alchemy;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class StasisolEffect extends AetherEffect{

	@Override
	protected Block soilBlock(){
		return Blocks.SNOW_BLOCK;
	}

	@Override
	protected Block rockBlock(){
		return Blocks.PACKED_ICE;
	}

	@Override
	protected Block crystalBlock(){
		return Blocks.BLUE_ICE;
	}

	@Override
	protected Block fluidBlock(){
		return Blocks.ICE;
	}

	@Override
	protected Block woodBlock(){
		return Blocks.SPRUCE_LOG;
	}

	@Override
	protected Block foliageBlock(){
		return Blocks.SPRUCE_LEAVES;
	}

	@Override
	protected RegistryKey<Biome> biome(){
		return Biomes.SNOWY_TUNDRA;
	}

	@Override
	public ITextComponent getName(){
		return new TranslationTextComponent("effect.terraform_snow");
	}
}

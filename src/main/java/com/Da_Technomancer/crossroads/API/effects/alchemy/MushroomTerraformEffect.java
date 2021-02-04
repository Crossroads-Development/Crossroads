package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.fluids.CRFluids;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

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
	protected RegistryKey<Biome> biome(){
		return Biomes.MUSHROOM_FIELDS;
	}

	@Override
	public ITextComponent getName(){
		return new TranslationTextComponent("effect.terraform_mushroom");
	}
}

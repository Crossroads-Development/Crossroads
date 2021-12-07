package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.fluids.CRFluids;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class FusasEffect extends AetherEffect{

	@Override
	protected Block soilBlock(){
		return Blocks.CLAY;
	}

	@Override
	protected Block rockBlock(){
		return Blocks.PRISMARINE;
	}

	@Override
	protected Block crystalBlock(){
		return Blocks.SEA_LANTERN;
	}

	@Override
	protected Block fluidBlock(){
		return CRFluids.distilledWater.block;
	}

	@Override
	protected Block woodBlock(){
		return Blocks.BUBBLE_CORAL_BLOCK;
	}

	@Override
	protected Block foliageBlock(){
		return Blocks.AIR;
	}

	@Override
	protected ResourceKey<Biome> biome(){
		return Biomes.DEEP_OCEAN;
	}

	@Override
	public Component getName(){
		return new TranslatableComponent("effect.terraform_ocean");
	}
}

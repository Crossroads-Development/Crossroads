package com.Da_Technomancer.crossroads.world;

import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.registries.IForgeRegistry;

public class CRWorldGen{

	public static SingleGen GEM_FEATURE;

	public static void register(IForgeRegistry<Feature<?>> reg){
		/* TODO disabled until Forge adds a hook

		//Create a new feature type for placing single blocks of ore
		reg.register((GEM_FEATURE = new SingleGen()).setRegistryName("single_gen"));

		for(Biome biome : Registry.BIOMES){
			//Copper ore gen, doesn't spawn in nether or end category biomes
			if(CRConfig.genCopperOre.get() && isOverworld(biome)){
				biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, configuredFeature(Feature.ORE, OreFeatureConfig.FillerBlockType.field_241882_a, OreSetup.oreCopper.getDefaultState(), 18, 0, 30, 2));
			}
			//Tin ore gen, doesn't spawn in nether or end category biomes, spawns as single ores
			if(CRConfig.genTinOre.get() && isOverworld(biome)){
				biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, configuredFeature(GEM_FEATURE, OreFeatureConfig.FillerBlockType.field_241882_a, OreSetup.oreTin.getDefaultState(), 1, 0, 30, 20));
			}
			//Ruby ore gen, spawn in nether category biomes, in nether quartz ore
			//The reason the spawn attempts is so high for rubies is that it can only generate in quartz ore.
			//The average number of quartz ore per chunk divided by the number of blockspaces in the given height range (heights nether quartz spawns at) is about 1/350, so 25000 tries will give an average of about 25 rubies per chunk by default.
			//Happy Mining!
			if(CRConfig.genRubyOre.get() && biome.getCategory() == Biome.Category.NETHER){
				biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, configuredFeature(GEM_FEATURE, SingleGen.NETHER_QUARTZ, OreSetup.oreRuby.getDefaultState(), 1, 8, 118, CRConfig.rubyRarity.get()));
			}
			//Void crystal gen, spawn in end category biomes, in endstone
			if(CRConfig.genVoidOre.get() && biome.getCategory() == Biome.Category.THEEND){
				biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, configuredFeature(GEM_FEATURE, SingleGen.ENDSTONE, OreSetup.oreVoid.getDefaultState(), 1, 5, 80, 10));
			}
		}
		*/
	}

	private static ConfiguredFeature<?, ?> configuredFeature(Feature<OreFeatureConfig> feature, RuleTest canOverwrite, BlockState ore, int veinSize, int minHeight, int maxHeight, int attemptsPerChunk){
		//MCP note: use whatever iron ore uses in the vanilla Features class
		return feature.withConfiguration(new OreFeatureConfig(canOverwrite, ore, veinSize)).withPlacement(rangePlacement(minHeight, maxHeight)).func_242728_a().func_242731_b(attemptsPerChunk);
	}

	private static ConfiguredPlacement<TopSolidRangeConfig> rangePlacement(int minHeight, int maxHeight){
		//MCP note: range placement
		return Placement.field_242907_l.configure(new TopSolidRangeConfig(minHeight, minHeight, maxHeight));
	}

	private static boolean isOverworld(Biome b){
		return b.getCategory() != Biome.Category.NETHER && b.getCategory() != Biome.Category.THEEND && b.getCategory() != Biome.Category.NONE;
	}
}

package com.Da_Technomancer.crossroads.world;

import com.Da_Technomancer.crossroads.API.CRReflection;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import com.Da_Technomancer.essentials.ReflectionUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CRWorldGen{

	public static SingleGen GEM_FEATURE;

	public static void register(IForgeRegistry<Feature<?>> reg){
		//Create a new feature type for placing single blocks of ore
		reg.register((GEM_FEATURE = new SingleGen()).setRegistryName("single_gen"));

		for(Biome biome : ForgeRegistries.BIOMES){
			//Copper ore gen, doesn't spawn in nether or end category biomes
			if(CRConfig.genCopperOre.get() && isOverworld(biome)){
//				addFeatureToBiome(biome, GenerationStage.Decoration.UNDERGROUND_ORES, configuredFeature(Feature.ORE, OreFeatureConfig.FillerBlockType.field_241882_a, OreSetup.oreCopper.getDefaultState(), 18, 0, 30, 2));
//				biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, configuredFeature(Feature.ORE, OreFeatureConfig.FillerBlockType.field_241882_a, OreSetup.oreCopper.getDefaultState(), 18, 0, 30, 2));
			}
			//Tin ore gen, doesn't spawn in nether or end category biomes, spawns as single ores
			if(CRConfig.genTinOre.get() && isOverworld(biome)){
//				addFeatureToBiome(biome, GenerationStage.Decoration.UNDERGROUND_ORES, configuredFeature(GEM_FEATURE, OreFeatureConfig.FillerBlockType.field_241882_a, OreSetup.oreTin.getDefaultState(), 1, 0, 30, 20));
//				biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, configuredFeature(GEM_FEATURE, OreFeatureConfig.FillerBlockType.field_241882_a, OreSetup.oreTin.getDefaultState(), 1, 0, 30, 20));
			}
			//Ruby ore gen, spawn in nether category biomes, in nether quartz ore
			//The reason the spawn attempts is so high for rubies is that it can only generate in quartz ore.
			//The average number of quartz ore per chunk divided by the number of blockspaces in the given height range (heights nether quartz spawns at) is about 1/350, so 25000 tries will give an average of about 25 rubies per chunk by default.
			//Happy Mining!
			if(CRConfig.genRubyOre.get() && biome.getCategory() == Biome.Category.NETHER){
//				addFeatureToBiome(biome, GenerationStage.Decoration.UNDERGROUND_ORES, configuredFeature(GEM_FEATURE, SingleGen.NETHER_QUARTZ, OreSetup.oreRuby.getDefaultState(), 1, 8, 118, CRConfig.rubyRarity.get()));
//				biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, configuredFeature(GEM_FEATURE, SingleGen.NETHER_QUARTZ, OreSetup.oreRuby.getDefaultState(), 1, 8, 118, CRConfig.rubyRarity.get()));
			}
			//Void crystal gen, spawn in end category biomes, in endstone
			if(CRConfig.genVoidOre.get() && biome.getCategory() == Biome.Category.THEEND){
//				addFeatureToBiome(biome, GenerationStage.Decoration.UNDERGROUND_ORES, configuredFeature(GEM_FEATURE, SingleGen.ENDSTONE, OreSetup.oreVoid.getDefaultState(), 1, 5, 80, 10));
//				biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, configuredFeature(GEM_FEATURE, SingleGen.ENDSTONE, OreSetup.oreVoid.getDefaultState(), 1, 5, 80, 10));
			}
		}
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

	//Everything below this line is borrowed code that will hopefully be made obsolete by Forge adding world gen hooks
	//TODO

	private static final Field BIOME_FEATURE_LIST = ReflectionUtil.reflectField(CRReflection.BIOME_FEATURE_LIST);

	private static void addFeatureToBiome(Biome biome, GenerationStage.Decoration feature, ConfiguredFeature<?, ?> configuredFeature){
		List<List<Supplier<ConfiguredFeature<?, ?>>>> biomeFeatures = convertImmutableFeatures(biome);
		if(biomeFeatures == null){
			return;
		}
		while(biomeFeatures.size() <= feature.ordinal()) {
			biomeFeatures.add(Lists.newArrayList());
		}
		biomeFeatures.get(feature.ordinal()).add(() -> configuredFeature);
	}

	@Nullable
	@Deprecated
	private static List<List<Supplier<ConfiguredFeature<?, ?>>>> convertImmutableFeatures(Biome biome){
		BiomeGenerationSettings generationSettings = biome.func_242440_e();
		List<List<Supplier<ConfiguredFeature<?, ?>>>> featureList = generationSettings.func_242498_c();
		if(featureList instanceof ImmutableList){
			featureList = featureList.stream().map(Lists::newArrayList).collect(Collectors.toList());
			if(BIOME_FEATURE_LIST == null){
				return null;
			}
			try{
				BIOME_FEATURE_LIST.set(generationSettings, featureList);
			}catch(IllegalAccessException e){
				return null;
			}
		}
		return featureList;
	}
}

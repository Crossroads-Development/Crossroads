package com.Da_Technomancer.crossroads.world;

import com.Da_Technomancer.crossroads.API.CRReflection;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import com.Da_Technomancer.essentials.ReflectionUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CRWorldGen{

	private static final SingleGen GEM_FEATURE = new SingleGen();
	private static final RubyGen RUBY_FEATURE = new RubyGen();

	private static final RuleTest ENDSTONE = new TagMatchRuleTest(Tags.Blocks.END_STONES);

	private static final ConfiguredFeature<?, ?> COPPER_ORE = configuredFeature(Feature.ORE, OreFeatureConfig.FillerBlockType.field_241882_a, OreSetup.oreCopper.getDefaultState(), 20, 32, 2);
	private static final ConfiguredFeature<?, ?> TIN_ORE = configuredFeature(GEM_FEATURE, OreFeatureConfig.FillerBlockType.field_241882_a, OreSetup.oreTin.getDefaultState(), 1, 32, 20);
	private static final ConfiguredFeature<?, ?> VOID_ORE = configuredFeature(GEM_FEATURE, ENDSTONE, OreSetup.oreVoid.getDefaultState(), 1, 80, 10);
	private static final ConfiguredFeature<?, ?> RUBY_ORE = RUBY_FEATURE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.field_241883_b, Blocks.NETHER_QUARTZ_ORE.getDefaultState(), 14)).withPlacement(Features.Placements.field_243998_i).func_242728_a().func_242731_b(16);//Normal nether quartz vein version
	private static final ConfiguredFeature<?, ?> RUBY_ORE_BASALT = RUBY_FEATURE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.field_241883_b, Blocks.NETHER_QUARTZ_ORE.getDefaultState(), 14)).withPlacement(Features.Placements.field_243998_i).func_242728_a().func_242731_b(32);//Basalt delta vein version

	public static void register(IForgeRegistry<Feature<?>> reg){
		//Create a new feature type for placing single blocks of ore
		reg.register(GEM_FEATURE.setRegistryName("single_gen"));
		reg.register(RUBY_FEATURE.setRegistryName("ruby_gen"));

		Registry.register(WorldGenRegistries.field_243653_e, new ResourceLocation(Crossroads.MODID, "ore_quartz_nether_ruby"), RUBY_ORE);
		Registry.register(WorldGenRegistries.field_243653_e, new ResourceLocation(Crossroads.MODID, "ore_quartz_deltas_ruby"), RUBY_ORE_BASALT);
		Registry.register(WorldGenRegistries.field_243653_e, new ResourceLocation(Crossroads.MODID, "ore_copper"), COPPER_ORE);
		Registry.register(WorldGenRegistries.field_243653_e, new ResourceLocation(Crossroads.MODID, "ore_tin"), TIN_ORE);
		Registry.register(WorldGenRegistries.field_243653_e, new ResourceLocation(Crossroads.MODID, "ore_void"), VOID_ORE);
	}

	public static void inject(){
		for(Biome biome : ForgeRegistries.BIOMES){
			if(CRConfig.genCopperOre.get() && isOverworld(biome)){
				addFeatureToBiome(biome, GenerationStage.Decoration.UNDERGROUND_ORES, COPPER_ORE);
//				biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, configuredFeature(Feature.ORE, OreFeatureConfig.FillerBlockType.field_241882_a, OreSetup.oreCopper.getDefaultState(), 18, 0, 30, 2));
			}
			//Tin ore gen, doesn't spawn in nether or end category biomes, spawns as single ores
			if(CRConfig.genTinOre.get() && isOverworld(biome)){
				addFeatureToBiome(biome, GenerationStage.Decoration.UNDERGROUND_ORES, TIN_ORE);
//				biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, configuredFeature(GEM_FEATURE, OreFeatureConfig.FillerBlockType.field_241882_a, OreSetup.oreTin.getDefaultState(), 1, 0, 30, 20));
			}
			//Void crystal gen, spawn in end category biomes, in endstone
			if(CRConfig.genVoidOre.get() && biome.getCategory() == Biome.Category.THEEND){
				addFeatureToBiome(biome, GenerationStage.Decoration.UNDERGROUND_ORES, VOID_ORE);
//				biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, configuredFeature(GEM_FEATURE, SingleGen.ENDSTONE, OreSetup.oreVoid.getDefaultState(), 1, 5, 80, 10));
			}

			if(biome.getCategory() == Biome.Category.NETHER){
				//Instead of adding ruby ore as a new feature to generate on top of nether quartz ore, we replace the vanilla nether quartz ore gen with a version that also makes rubies
				replaceFeature(biome, GenerationStage.Decoration.UNDERGROUND_DECORATION, RUBY_ORE, Features.field_243889_bi);
				replaceFeature(biome, GenerationStage.Decoration.UNDERGROUND_DECORATION, RUBY_ORE_BASALT, Features.field_243887_bg);
			}
		}
	}

	private static ConfiguredFeature<?, ?> configuredFeature(Feature<OreFeatureConfig> feature, RuleTest canOverwrite, BlockState ore, int veinSize, int maxHeight, int attemptsPerChunk){
		//MCP note: use whatever iron ore uses in the vanilla Features class
		return feature.withConfiguration(new OreFeatureConfig(canOverwrite, ore, veinSize)).func_242733_d(maxHeight).func_242728_a().func_242731_b(attemptsPerChunk);
	}

	private static boolean isOverworld(Biome b){
		return b.getCategory() != Biome.Category.NETHER && b.getCategory() != Biome.Category.THEEND && b.getCategory() != Biome.Category.NONE;
	}

	//Everything below this line is borrowed code that will hopefully be made obsolete by Forge adding world gen hooks
	//TODO

	private static final Field BIOME_FEATURE_LIST = ReflectionUtil.reflectField(CRReflection.BIOME_FEATURE_LIST);

	private static void replaceFeature(Biome biome, GenerationStage.Decoration generationStage, ConfiguredFeature<?, ?> toInsert, ConfiguredFeature<?, ?> toRemove){
		List<List<Supplier<ConfiguredFeature<?, ?>>>> biomeFeatures = convertImmutableFeatures(biome);
		if(biomeFeatures == null){
			return;
		}
		int stageOrdinal = generationStage.ordinal();
		if(biomeFeatures.size() <= stageOrdinal){
			return;//Nothing to replace
		}
		List<Supplier<ConfiguredFeature<?, ?>>> stageFeatures = biomeFeatures.get(stageOrdinal);
		for(int i = 0; i < stageFeatures.size(); i++){
			if(stageFeatures.get(i).get().equals(toRemove)){
				stageFeatures.set(i, () -> toInsert);
				return;
			}
		}
	}

	private static void addFeatureToBiome(Biome biome, GenerationStage.Decoration generationStage, ConfiguredFeature<?, ?> configuredFeature){
		List<List<Supplier<ConfiguredFeature<?, ?>>>> biomeFeatures = convertImmutableFeatures(biome);
		if(biomeFeatures == null){
			return;
		}
		int stageOrdinal = generationStage.ordinal();
		while(biomeFeatures.size() <= stageOrdinal){
			biomeFeatures.add(Lists.newArrayList());
		}
		biomeFeatures.get(stageOrdinal).add(() -> configuredFeature);
	}

	@Nullable
	@Deprecated
	private static List<List<Supplier<ConfiguredFeature<?, ?>>>> convertImmutableFeatures(Biome biome){
		BiomeGenerationSettings generationSettings = biome.func_242440_e();
		List<List<Supplier<ConfiguredFeature<?, ?>>>> featureList = generationSettings.func_242498_c();
		if(featureList instanceof ImmutableList){
			if(BIOME_FEATURE_LIST == null){
				return null;
			}
			featureList = featureList.stream().map(Lists::newArrayList).collect(Collectors.toList());
			try{
				BIOME_FEATURE_LIST.set(generationSettings, featureList);
			}catch(IllegalAccessException e){
				return null;
			}
		}
		return featureList;
	}
}

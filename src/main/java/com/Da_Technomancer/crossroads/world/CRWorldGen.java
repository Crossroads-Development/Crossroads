package com.Da_Technomancer.crossroads.world;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class CRWorldGen{

	private static final SingleGen GEM_FEATURE = new SingleGen();
//	private static final RubyGen RUBY_FEATURE = new RubyGen();

	private static ConfiguredFeature<?, ?> COPPER_ORE;
	private static ConfiguredFeature<?, ?> TIN_ORE;
	private static ConfiguredFeature<?, ?> VOID_ORE;
	private static ConfiguredFeature<?, ?> RUBY_ORE_SPOT;//Ruby is currently generating as single block ores scattered randomly in netherrack
//	private static final ConfiguredFeature<?, ?> RUBY_ORE = RUBY_FEATURE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, Blocks.NETHER_QUARTZ_ORE.getDefaultState(), 14)).withPlacement(Features.Placements.RANGE_10_20_ROOFED).squared().count(16);//Normal nether quartz vein version
//	private static final ConfiguredFeature<?, ?> RUBY_ORE_BASALT = RUBY_FEATURE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, Blocks.NETHER_QUARTZ_ORE.getDefaultState(), 14)).withPlacement(Features.Placements.RANGE_10_20_ROOFED).squared().count(32);//Basalt delta vein version

	/**
	 * Must be called before register(), on both sides
	 */
	public static void init(){
		//Register the relevant config options to be used for worldgen with ConfigTagRuleTest
		ConfigTagRuleTest.registerConfig("cr_copper", CRConfig.genCopperOre);
		ConfigTagRuleTest.registerConfig("cr_tin", CRConfig.genTinOre);
		ConfigTagRuleTest.registerConfig("cr_ruby", CRConfig.genRubyOre);
		ConfigTagRuleTest.registerConfig("cr_void", CRConfig.genVoidOre);
		//Construct our configured features
		COPPER_ORE = configuredFeature(Feature.ORE, new ConfigTagRuleTest(BlockTags.BASE_STONE_OVERWORLD, "cr_copper"), OreSetup.oreCopper.defaultBlockState(), 13, 32, 3);
		TIN_ORE = configuredFeature(GEM_FEATURE, new ConfigTagRuleTest(BlockTags.BASE_STONE_OVERWORLD, "cr_tin"), OreSetup.oreTin.defaultBlockState(), 1, 32, 20);
		VOID_ORE = configuredFeature(GEM_FEATURE, new ConfigTagRuleTest(Tags.Blocks.END_STONES, "cr_void"), OreSetup.oreVoid.defaultBlockState(), 1, 80, 10);
		RUBY_ORE_SPOT = configuredFeature(GEM_FEATURE, new ConfigTagRuleTest(BlockTags.bind(Crossroads.MODID + ":netherrack"), "cr_ruby"), OreSetup.oreRuby.defaultBlockState(), 1, 117, 20);
	}

	/**
	 * Must be called after init() and before addWorldgen()
	 */
	public static void register(IForgeRegistry<Feature<?>> reg){
		//Create a new feature type for placing single blocks of ore
		reg.register(GEM_FEATURE.setRegistryName("single_gen"));
//		reg.register(RUBY_FEATURE.setRegistryName("ruby_gen"));

		Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(Crossroads.MODID, "ore_ruby_spot"), RUBY_ORE_SPOT);
//		Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(Crossroads.MODID, "ore_quartz_nether_ruby"), RUBY_ORE);
//		Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(Crossroads.MODID, "ore_quartz_deltas_ruby"), RUBY_ORE_BASALT);
		Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(Crossroads.MODID, "ore_copper"), COPPER_ORE);
		Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(Crossroads.MODID, "ore_tin"), TIN_ORE);
		Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(Crossroads.MODID, "ore_void"), VOID_ORE);
	}

	public static void addWorldgen(BiomeLoadingEvent event){
		if(isOverworld(event.getCategory())){
			event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, COPPER_ORE);
			event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, TIN_ORE);
		}else if(event.getCategory() == Biome.Category.THEEND){
			event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, VOID_ORE);
		}else if(event.getCategory() == Biome.Category.NETHER){
			event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, RUBY_ORE_SPOT);
		}
	}

	private static ConfiguredFeature<?, ?> configuredFeature(Feature<OreFeatureConfig> feature, RuleTest canOverwrite, BlockState ore, int veinSize, int maxHeight, int attemptsPerChunk){
		//MCP note: use whatever iron ore uses in the vanilla Features class
		return feature.configured(new OreFeatureConfig(canOverwrite, ore, veinSize)).range(maxHeight).squared().count(attemptsPerChunk);
	}

	private static boolean isOverworld(Biome.Category cat){
		return cat != Biome.Category.NETHER && cat != Biome.Category.THEEND && cat != Biome.Category.NONE;
	}
}

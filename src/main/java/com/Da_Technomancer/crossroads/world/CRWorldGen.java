package com.Da_Technomancer.crossroads.world;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class CRWorldGen{

	private static final SingleGen GEM_FEATURE = new SingleGen();
//	private static final RubyGen RUBY_FEATURE = new RubyGen();

	private static final RuleTest ENDSTONE = new TagMatchRuleTest(Tags.Blocks.END_STONES);

	private static final ConfiguredFeature<?, ?> COPPER_ORE = configuredFeature(Feature.ORE, OreFeatureConfig.FillerBlockType.field_241882_a, OreSetup.oreCopper.getDefaultState(), 13, 32, 3);
	private static final ConfiguredFeature<?, ?> TIN_ORE = configuredFeature(GEM_FEATURE, OreFeatureConfig.FillerBlockType.field_241882_a, OreSetup.oreTin.getDefaultState(), 1, 32, 20);
	private static final ConfiguredFeature<?, ?> VOID_ORE = configuredFeature(GEM_FEATURE, ENDSTONE, OreSetup.oreVoid.getDefaultState(), 1, 80, 10);
	private static final ConfiguredFeature<?, ?> RUBY_ORE_SPOT = configuredFeature(GEM_FEATURE, OreFeatureConfig.FillerBlockType.field_241883_b, OreSetup.oreRuby.getDefaultState(), 1, 117, 20);
//	private static final ConfiguredFeature<?, ?> RUBY_ORE = RUBY_FEATURE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.field_241883_b, Blocks.NETHER_QUARTZ_ORE.getDefaultState(), 14)).withPlacement(Features.Placements.field_243998_i).func_242728_a().func_242731_b(16);//Normal nether quartz vein version
//	private static final ConfiguredFeature<?, ?> RUBY_ORE_BASALT = RUBY_FEATURE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.field_241883_b, Blocks.NETHER_QUARTZ_ORE.getDefaultState(), 14)).withPlacement(Features.Placements.field_243998_i).func_242728_a().func_242731_b(32);//Basalt delta vein version

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
			if(CRConfig.genCopperOre.get()){
				event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, COPPER_ORE);
			}
			if(CRConfig.genTinOre.get()){
				event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, TIN_ORE);
			}
		}else if(event.getCategory() == Biome.Category.THEEND){
			event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, VOID_ORE);
		}else if(event.getCategory() == Biome.Category.NETHER){
			event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, RUBY_ORE_SPOT);
		}
	}

	private static ConfiguredFeature<?, ?> configuredFeature(Feature<OreFeatureConfig> feature, RuleTest canOverwrite, BlockState ore, int veinSize, int maxHeight, int attemptsPerChunk){
		//MCP note: use whatever iron ore uses in the vanilla Features class
		return feature.withConfiguration(new OreFeatureConfig(canOverwrite, ore, veinSize)).func_242733_d(maxHeight).func_242728_a().func_242731_b(attemptsPerChunk);
	}

	private static boolean isOverworld(Biome.Category cat){
		return cat != Biome.Category.NETHER && cat != Biome.Category.THEEND && cat != Biome.Category.NONE;
	}
}

package com.Da_Technomancer.crossroads.world;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;

public class CRWorldGen{

	private static final SingleGen GEM_FEATURE = new SingleGen();

	private static ConfiguredFeature<?, ?> TIN_ORE_BURIED;
	private static ConfiguredFeature<?, ?> VOID_ORE;
	private static ConfiguredFeature<?, ?> RUBY_ORE;//Ruby is currently generating as single block ores scattered randomly in netherrack

	private static PlacedFeature TIN_ORE_PLACED_TRIANGLE;
	private static PlacedFeature TIN_ORE_PLACED_LOWER;
	private static PlacedFeature VOID_ORE_PLACED;
	private static PlacedFeature RUBY_ORE_PLACED;

	/**
	 * Must be called before register(), on both sides
	 */
	public static void init(){
		//Register the relevant config options to be used for worldgen with ConfigTagRuleTest
		ConfigTagRuleTest.registerConfig("cr_tin", CRConfig.genTinOre);
		ConfigTagRuleTest.registerConfig("cr_ruby", CRConfig.genRubyOre);
		ConfigTagRuleTest.registerConfig("cr_void", CRConfig.genVoidOre);


		//Construct our configured features

		//Both deepslate and normal variants. Size 1 vein
		//50% chance to discard if exposed to air for buried
		List<OreConfiguration.TargetBlockState> tinTarget = List.of(OreConfiguration.target(new ConfigTagRuleTest(BlockTags.STONE_ORE_REPLACEABLES, "cr_tin"), OreSetup.oreTin.defaultBlockState()), OreConfiguration.target(new ConfigTagRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES, "cr_tin"), OreSetup.oreTinDeep.defaultBlockState()));
		TIN_ORE_BURIED = GEM_FEATURE.configured(new OreConfiguration(tinTarget, 1, 0.5F));
		//Size 1 vein
		VOID_ORE = GEM_FEATURE.configured(new OreConfiguration(new ConfigTagRuleTest(Tags.Blocks.END_STONES, "cr_void"), OreSetup.oreVoid.defaultBlockState(), 1));
		//Size 1 vein
		RUBY_ORE = GEM_FEATURE.configured(new OreConfiguration(new ConfigTagRuleTest(BlockTags.bind(Crossroads.MODID + ":blackstone"), "cr_ruby"), OreSetup.oreRuby.defaultBlockState(), 1));

		//Placement construction
		TIN_ORE_PLACED_TRIANGLE = TIN_ORE_BURIED.placed(commonOrePlacement(24, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(32))));
		TIN_ORE_PLACED_LOWER = TIN_ORE_BURIED.placed(commonOrePlacement(3, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(-48))));
		VOID_ORE_PLACED = VOID_ORE.placed(commonOrePlacement(12, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(80))));
		RUBY_ORE_PLACED = RUBY_ORE.placed(commonOrePlacement(100, HeightRangePlacement.uniform(VerticalAnchor.absolute(2), VerticalAnchor.absolute(117))));
	}

	/**
	 * Must be called after init() and before addWorldgen()
	 */
	public static void register(IForgeRegistry<Feature<?>> reg){
		//Create a new feature type for placing single blocks of ore
		reg.register(GEM_FEATURE.setRegistryName("single_gen"));
//		reg.register(RUBY_FEATURE.setRegistryName("ruby_gen"));

		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(Crossroads.MODID, "ore_ruby_spot"), RUBY_ORE);
//		Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(Crossroads.MODID, "ore_quartz_deltas_ruby"), RUBY_ORE_BASALT);
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(Crossroads.MODID, "ore_tin_buried"), TIN_ORE_BURIED);
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(Crossroads.MODID, "ore_void"), VOID_ORE);

		Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(Crossroads.MODID, "ore_tin_triangle"), TIN_ORE_PLACED_TRIANGLE);
		Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(Crossroads.MODID, "ore_tin_triangle"), TIN_ORE_PLACED_LOWER);
		Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(Crossroads.MODID, "ore_void"), VOID_ORE_PLACED);
		Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(Crossroads.MODID, "ore_ruby"), RUBY_ORE_PLACED);
	}

	public static void addWorldgen(BiomeLoadingEvent event){
		if(isOverworld(event.getCategory())){
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TIN_ORE_PLACED_TRIANGLE);
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TIN_ORE_PLACED_LOWER);
		}else if(event.getCategory() == Biome.BiomeCategory.THEEND){
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, VOID_ORE_PLACED);
		}else if(event.getCategory() == Biome.BiomeCategory.NETHER){
			//Ruby ore is placed in blackstone, which is generated during UNDERGROUND_DECORATION
			//We therefore add ruby as part of the new generation step, as UNDERGROUND_ORES is too early
			event.getGeneration().addFeature(GenerationStep.Decoration.FLUID_SPRINGS, RUBY_ORE_PLACED);
		}
	}

	private static boolean isOverworld(Biome.BiomeCategory cat){
		return cat != Biome.BiomeCategory.NETHER && cat != Biome.BiomeCategory.THEEND && cat != Biome.BiomeCategory.NONE;
	}

	private static List<PlacementModifier> orePlacement(PlacementModifier attemptsPerChunk, PlacementModifier distribution){
		return List.of(attemptsPerChunk, InSquarePlacement.spread(), distribution, BiomeFilter.biome());
	}

	private static List<PlacementModifier> commonOrePlacement(int attemptsPerChunk, PlacementModifier distribution){
		return orePlacement(CountPlacement.of(attemptsPerChunk), distribution);
	}
}

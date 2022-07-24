package com.Da_Technomancer.crossroads.world;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.items.item_sets.OreSetup;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.List;

public class CRWorldGen{

	private static final SingleGen GEM_FEATURE = new SingleGen();//Create a new feature type for placing single blocks of ore
	public static final NoOpFalseFeature NONE_FEATURE = new NoOpFalseFeature();

	public static Holder<ConfiguredFeature<OreConfiguration, ?>> TIN_ORE_BURIED;
	public static Holder<ConfiguredFeature<OreConfiguration, ?>> VOID_ORE;
	public static Holder<ConfiguredFeature<OreConfiguration, ?>> RUBY_ORE;//Ruby is currently generating as single block ores scattered randomly in netherrack
	public static Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> EMPTY;

	/**
	 * Must be called before register(), on both sides
	 */
	public static void init(){
		//Register the features
		toRegister.put("single_gen", GEM_FEATURE);
		toRegister.put("no_op_false", NONE_FEATURE);

		//Register the relevant config options to be used for worldgen with ConfigTagRuleTest
		ConfigTagRuleTest.registerConfig("cr_tin", CRConfig.genTinOre);
		ConfigTagRuleTest.registerConfig("cr_ruby", CRConfig.genRubyOre);
		ConfigTagRuleTest.registerConfig("cr_void", CRConfig.genVoidOre);

		//Construct our configured features

		//Both deepslate and normal variants. Size 1 vein
		//50% chance to discard if exposed to air for buried
		List<OreConfiguration.TargetBlockState> tinTarget = List.of(OreConfiguration.target(new ConfigTagRuleTest(BlockTags.STONE_ORE_REPLACEABLES, "cr_tin"), OreSetup.oreTin.defaultBlockState()), OreConfiguration.target(new ConfigTagRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES, "cr_tin"), OreSetup.oreTinDeep.defaultBlockState()));
		TIN_ORE_BURIED = createAndRegisterConfiguredFeature(new ResourceLocation(Crossroads.MODID, "ore_tin_buried"), GEM_FEATURE, new OreConfiguration(tinTarget, 1, 0.5F));
		//Size 1 vein
		VOID_ORE = createAndRegisterConfiguredFeature(new ResourceLocation(Crossroads.MODID, "ore_void"), GEM_FEATURE, new OreConfiguration(new ConfigTagRuleTest(Tags.Blocks.END_STONES, "cr_void"), OreSetup.oreVoid.defaultBlockState(), 1));
		//Size 1 vein
		RUBY_ORE = createAndRegisterConfiguredFeature(new ResourceLocation(Crossroads.MODID, "ore_ruby_spot"), GEM_FEATURE, new OreConfiguration(new ConfigTagRuleTest(CraftingUtil.getTagKey(ForgeRegistries.Keys.BLOCKS, new ResourceLocation(Crossroads.MODID, "blackstone")), "cr_ruby"), OreSetup.oreRuby.defaultBlockState(), 1));
		//Does nothing
		EMPTY = createAndRegisterConfiguredFeature(new ResourceLocation(Crossroads.MODID, "empty"), NONE_FEATURE, FeatureConfiguration.NONE);

		//Placement construction and registration
		createAndRegisterPlacedFeature(new ResourceLocation(Crossroads.MODID, "ore_tin_triangle"), TIN_ORE_BURIED, commonOrePlacement(24, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(32))));
		createAndRegisterPlacedFeature(new ResourceLocation(Crossroads.MODID, "ore_tin_lower"), TIN_ORE_BURIED, commonOrePlacement(3, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(-48))));
		createAndRegisterPlacedFeature(new ResourceLocation(Crossroads.MODID, "ore_void"), VOID_ORE, commonOrePlacement(12, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(80))));
		createAndRegisterPlacedFeature(new ResourceLocation(Crossroads.MODID, "ore_ruby"), RUBY_ORE, commonOrePlacement(100, HeightRangePlacement.uniform(VerticalAnchor.absolute(2), VerticalAnchor.absolute(117))));
	}

	public static final HashMap<String, Feature<?>> toRegister = new HashMap<>();

	private static List<PlacementModifier> orePlacement(PlacementModifier attemptsPerChunk, PlacementModifier distribution){
		return List.of(attemptsPerChunk, InSquarePlacement.spread(), distribution, BiomeFilter.biome());
	}

	private static List<PlacementModifier> commonOrePlacement(int attemptsPerChunk, PlacementModifier distribution){
		return orePlacement(CountPlacement.of(attemptsPerChunk), distribution);
	}

	public static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<FC, ?>> createAndRegisterConfiguredFeature(ResourceLocation id, F feature, FC configuration){
		return FeatureUtils.register(id.toString(), feature, configuration);
	}

	private static Holder<PlacedFeature> createAndRegisterPlacedFeature(ResourceLocation id, Holder<? extends ConfiguredFeature<?, ?>> feature, List<PlacementModifier> placement) {
		return PlacementUtils.register(id.toString(), feature, placement);
	}
}

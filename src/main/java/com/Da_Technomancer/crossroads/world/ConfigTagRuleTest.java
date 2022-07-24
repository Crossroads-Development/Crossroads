package com.Da_Technomancer.crossroads.world;

import com.Da_Technomancer.crossroads.Crossroads;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.function.Function;

/**
 * Similar to a TagMatchRuleTest for worldgen, except each instance has a config value specified, and worldgen will only be allowed while the config is true.
 * Configs need to be registered on BOTH sides with registerConfig before use with the constructor
 */
public class ConfigTagRuleTest extends TagMatchTest{

	//Literally this should have just been a bunch of lambdas, but for reasons unknown, that fails at runtime
	//However, it works if explicitly declared as classes (see bottom of this file)
	//This should not be necessary, but it is

//	public static final Codec<ConfigTagRuleTest> CONFIG_CODEC = RecordCodecBuilder.create(
//			(instance) -> instance.group(
//					ITag.codec(() -> TagCollectionManager.getInstance().getBlocks()).fieldOf("tag").forGetter(ConfigTagRuleTest::getTag),
//					Codec.STRING.fieldOf("config_name").forGetter(ConfigTagRuleTest::getConfigName)
//			).apply(instance, ConfigTagRuleTest::new));

	public static final Codec<ConfigTagRuleTest> CONFIG_CODEC = RecordCodecBuilder.create(new CodecApplyFunction());

	public static final RuleTestType<ConfigTagRuleTest> TYPE = RuleTestType.register("tag_and_config_match", CONFIG_CODEC);
	public static final HashMap<String, ForgeConfigSpec.BooleanValue> configMap = new HashMap<>(3);

	public static void registerConfig(String configName, ForgeConfigSpec.BooleanValue controllingConfig){
		configMap.put(configName, controllingConfig);
	}

	private final String configName;
	private final ForgeConfigSpec.BooleanValue config;
	private final TagKey<Block> tag;//private in the superclass

	public ConfigTagRuleTest(TagKey<Block> tag, String configName){
		super(tag);
		this.tag = tag;
		this.configName = configName;
		config = configMap.getOrDefault(configName, null);
		if(config == null){
			Crossroads.logger.error("Missing ConfigTagRuleTest config registration %1$s; Defaulting to enabled config", configName);
//			assert false;//For testing
		}
	}

	public TagKey<Block> getTag(){
		return tag;
	}

	public String getConfigName(){
		return configName;
	}

	@Override
	public boolean test(BlockState state, RandomSource rand){
		return (config == null || config.get()) && super.test(state, rand);
	}

	@Override
	protected RuleTestType<?> getType(){
		return TYPE;
	}

	private static class CodecGroupFunction implements Function<RecordCodecBuilder.Instance<ConfigTagRuleTest>, Products.P2<RecordCodecBuilder.Mu<ConfigTagRuleTest>, TagKey<Block>, String>>{

		@Override
		public Products.P2<RecordCodecBuilder.Mu<ConfigTagRuleTest>, TagKey<Block>, String> apply(RecordCodecBuilder.Instance<ConfigTagRuleTest> instance){
			return instance.group(
					TagKey.codec(ForgeRegistries.BLOCKS.getRegistryKey()).fieldOf("tag").forGetter(ConfigTagRuleTest::getTag),
					Codec.STRING.fieldOf("config_name").forGetter(ConfigTagRuleTest::getConfigName)
			);
		}
	}

	private static class CodecApplyFunction implements Function<RecordCodecBuilder.Instance<ConfigTagRuleTest>, App<RecordCodecBuilder.Mu<ConfigTagRuleTest>, ConfigTagRuleTest>>{

		private static final CodecGroupFunction groupFunct = new CodecGroupFunction();

		@Override
		public App<RecordCodecBuilder.Mu<ConfigTagRuleTest>, ConfigTagRuleTest> apply(RecordCodecBuilder.Instance<ConfigTagRuleTest> instance){
			return groupFunct.apply(instance).apply(instance, ConfigTagRuleTest::new);
		}
	}
}

package com.Da_Technomancer.crossroads.world;

import com.Da_Technomancer.crossroads.Crossroads;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.world.gen.feature.template.IRuleTestType;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Random;

/**
 * Similar to a TagMatchRuleTest for worldgen, except each instance has a config value specified, and worldgen will only be allowed while the config is true.
 * Configs need to be registered on BOTH sides with registerConfig before use with the constructor
 */
public class ConfigTagRuleTest extends TagMatchRuleTest{

	public static final Codec<ConfigTagRuleTest> CODEC = RecordCodecBuilder.create((instance) -> instance.group(ITag.codec(() -> TagCollectionManager.getInstance().getBlocks()).fieldOf("tag").forGetter(ConfigTagRuleTest::getTag), Codec.STRING.fieldOf("config_name").forGetter(ConfigTagRuleTest::getConfigName)).apply(instance, ConfigTagRuleTest::new));
	public static final IRuleTestType<ConfigTagRuleTest> TYPE = IRuleTestType.register("tag_and_config_match", CODEC);
	public static final HashMap<String, ForgeConfigSpec.BooleanValue> configMap = new HashMap<>(4);

	public static void registerConfig(String configName, ForgeConfigSpec.BooleanValue controllingConfig){
		configMap.put(configName, controllingConfig);
	}

	private final String configName;
	private final ForgeConfigSpec.BooleanValue config;
	private final ITag<Block> tag;//private in the superclass

	public ConfigTagRuleTest(ITag<Block> tag, String configName){
		super(tag);
		this.tag = tag;
		this.configName = configName;
		config = configMap.getOrDefault(configName, null);
		if(config == null){
			Crossroads.logger.error("Missing ConfigTagRuleTest config registration %1$s; Defaulting to enabled config", configName);
//			assert false;//For testing
		}
	}

	public ITag<Block> getTag(){
		return tag;
	}

	public String getConfigName(){
		return configName;
	}

	@Override
	public boolean test(BlockState state, Random rand){
		return (config == null || config.get()) && super.test(state, rand);
	}

	@Override
	protected IRuleTestType<?> getType(){
		return TYPE;
	}
}

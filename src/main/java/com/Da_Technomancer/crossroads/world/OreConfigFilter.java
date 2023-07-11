package com.Da_Technomancer.crossroads.world;

import com.Da_Technomancer.crossroads.Crossroads;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;

/**
 * A placement modifier for disabling placed_features based on a boolean-valued config
 * Only works for config options registered with registerConfig()
 */
public class OreConfigFilter extends PlacementFilter{

	private static final HashMap<String, ForgeConfigSpec.BooleanValue> configMap = new HashMap<>(3);

	protected static final Codec<OreConfigFilter> CODEC = RecordCodecBuilder.create((builder) ->
			builder.group(Codec.STRING.fieldOf("config").forGetter(configFilter -> configFilter.configName))
					.apply(builder, OreConfigFilter::new));


	public static void registerConfig(String configName, ForgeConfigSpec.BooleanValue controllingConfig){
		configMap.put(configName, controllingConfig);
	}

	private final String configName;
	private final ForgeConfigSpec.BooleanValue config;//cache

	private OreConfigFilter(String configName){
		this.configName = configName;
		config = configMap.getOrDefault(configName, null);
		if(config == null){
			Crossroads.logger.error("Missing ConfigTagRuleTest config registration %1$s; Defaulting to enabled config", configName);
		}
	}

	@Override
	protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos){
		return config == null || config.get();
	}

	@Override
	public PlacementModifierType<?> type(){
		return null;//TODO
	}
}

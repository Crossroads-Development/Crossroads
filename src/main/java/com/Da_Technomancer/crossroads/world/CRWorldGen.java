package com.Da_Technomancer.crossroads.world;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.HashMap;

public class CRWorldGen{

	public static final HashMap<String, Feature<?>> toRegisterFeature = new HashMap<>(2);
	public static final HashMap<String, PlacementModifierType<? extends PlacementModifier>> toRegisterModifier = new HashMap<>(1);

	//Maps to crossroads:configured_feature/empty.json, which is a feature that does nothing and returns false
	public static ResourceKey<ConfiguredFeature<?, ?>> EMPTY_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(Crossroads.MODID, "empty"));

	private static boolean hasInit = false;

	public static void init(){
		if(hasInit){
			return;
		}
		hasInit = true;

		toRegisterFeature.put("single_gen", new SingleGen());
		toRegisterFeature.put("no_op_false", new NoOpFalseFeature());

		toRegisterModifier.put("ore_config", (PlacementModifierType<OreConfigFilter>) (() -> OreConfigFilter.CODEC));

		//Register the relevant config options to be used for worldgen
		OreConfigFilter.registerConfig("cr_tin", CRConfig.genTinOre);
		OreConfigFilter.registerConfig("cr_ruby", CRConfig.genRubyOre);
		OreConfigFilter.registerConfig("cr_void", CRConfig.genVoidOre);
	}
}

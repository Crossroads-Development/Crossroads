package com.Da_Technomancer.crossroads.world;

import com.Da_Technomancer.crossroads.CrossroadsConfig;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class ModWorldGen{

	public static SingleGen GEM_FEATURE;

	public static void register(IForgeRegistry<Feature<?>> reg){
		//Create a new feature type for placing single blocks of ore
		reg.register(GEM_FEATURE = new SingleGen());

		for(Biome biome : ForgeRegistries.BIOMES){
			//Copper ore gen, doesn't spawn in nether or end category biomes
			if(CrossroadsConfig.genCopperOre.get() && isOverworld(biome)){
				biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createDecoratedFeature(Feature.ORE, new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, OreSetup.oreCopper.getDefaultState(), 18), Placement.COUNT_RANGE, new CountRangeConfig(2, 0, 0, 30)));
			}
			//Tin ore gen, doesn't spawn in nether or end category biomes, spawns as single ores
			if(CrossroadsConfig.genTinOre.get() && isOverworld(biome)){
				biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createDecoratedFeature(GEM_FEATURE, new SingleGen.SingleOreConfig(SingleGen.SingleOreConfig.CRFillerType.NATURAL_STONE, OreSetup.oreTin.getDefaultState()), Placement.COUNT_RANGE, new CountRangeConfig(20, 0, 0, 30)));
			}
			//Ruby ore gen, spawn in nether category biomes, in nether quartz ore
			//The reason the spawn attempts is so high for rubies is that it can only generate in quartz ore.
			//The average number of quartz ore per chunk divided by the number of blockspaces in the given height range (heights nether quartz spawns at) is about 1/350, so 1000 tries will give an average of about 1 ruby per chunk.
			//Happy Mining!
			if(CrossroadsConfig.genRubyOre.get() && biome.getCategory() == Biome.Category.NETHER){
				biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createDecoratedFeature(GEM_FEATURE, new SingleGen.SingleOreConfig(SingleGen.SingleOreConfig.CRFillerType.QUARTZ, OreSetup.oreRuby.getDefaultState()), Placement.COUNT_RANGE, new CountRangeConfig(1000, 8, 8, 118)));
			}
			//Void crystal gen, spawn in end category biomes, in endstone
			if(CrossroadsConfig.genVoidOre.get() && biome.getCategory() == Biome.Category.THEEND){
				biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createDecoratedFeature(GEM_FEATURE, new SingleGen.SingleOreConfig(SingleGen.SingleOreConfig.CRFillerType.END_STONE, OreSetup.oreVoid.getDefaultState()), Placement.COUNT_RANGE, new CountRangeConfig(10, 5, 5, 80)));
			}
		}
	}

	private static boolean isOverworld(Biome b){
		return b.getCategory() != Biome.Category.NETHER && b.getCategory() != Biome.Category.THEEND;
	}
}

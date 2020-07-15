package com.Da_Technomancer.crossroads.world;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraftforge.common.Tags;

import java.util.Random;

public class SingleGen extends Feature<OreFeatureConfig>{

	public static final OreFeatureConfig.FillerBlockType NETHER_QUARTZ;
	public static final OreFeatureConfig.FillerBlockType ENDSTONE;

	static{
		NETHER_QUARTZ = OreFeatureConfig.FillerBlockType.create("NETHER_QUARTZ", "nether_quartz", (BlockState b) -> b.getBlock().isIn(Tags.Blocks.ORES_QUARTZ));
		ENDSTONE = OreFeatureConfig.FillerBlockType.create("ENDSTONE", "endstone", (BlockState b) -> b.getBlock().isIn(Tags.Blocks.END_STONES));
	}

	protected SingleGen(){
		super(OreFeatureConfig.field_236566_a_);
	}

	@Override
	public boolean func_230362_a_(ISeedReader world, StructureManager structureManager, ChunkGenerator generator, Random rand, BlockPos pos, OreFeatureConfig config){
		BlockState state = world.getBlockState(pos);
		if(config.target.getTargetBlockPredicate().test(state)){
			world.setBlockState(pos, config.state, 2);
			return true;
		}
		return false;
	}
}

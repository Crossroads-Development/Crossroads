package com.Da_Technomancer.crossroads.world;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockMatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

import java.util.Random;
import java.util.function.Predicate;

public class SingleGen extends Feature<SingleGen.SingleOreConfig>{

	protected SingleGen(){
		super(SingleOreConfig::deserialize);
	}

	@Override
	public boolean place(IWorld worldIn, ChunkGenerator generator, Random rand, BlockPos pos, SingleGen.SingleOreConfig config){
		BlockState state = worldIn.getBlockState(pos);
		if(config.target.getPred().test(state)){
			worldIn.setBlockState(pos, config.state, 2);
			return true;
		}
		return false;
	}

	/**
	 * The vanilla version of this is OreFeatureConfig.class. However, that class is locked to a few discrete values for filler types
	 * So we make our own ore config with blackjack and hookers, and a different set of locked values
	 */
	public static class SingleOreConfig implements IFeatureConfig{
		public final SingleOreConfig.CRFillerType target;
		public final BlockState state;

		public SingleOreConfig(SingleOreConfig.CRFillerType target, BlockState state){
			this.state = state;
			this.target = target;
		}

		public <T> Dynamic<T> serialize(DynamicOps<T> ops){
			return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("target"), ops.createString(this.target.name()), ops.createString("state"), BlockState.serialize(ops, this.state).getValue())));
		}

		public static SingleOreConfig deserialize(Dynamic<?> dyn){
			SingleOreConfig.CRFillerType fillType = SingleOreConfig.CRFillerType.valueOf(dyn.get("target").asString(""));
			BlockState blockstate = dyn.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
			return new SingleOreConfig(fillType, blockstate);
		}

		public enum CRFillerType{
			NATURAL_STONE((state) -> {
				if(state == null){
					return false;
				}else{
					Block block = state.getBlock();
					return block == Blocks.STONE || block == Blocks.GRANITE || block == Blocks.DIORITE || block == Blocks.ANDESITE;
				}
			}),
			QUARTZ(new BlockMatcher(Blocks.NETHER_QUARTZ_ORE)),
			END_STONE(new BlockMatcher(Blocks.END_STONE));

			private final Predicate<BlockState> matched;

			CRFillerType(Predicate<BlockState> matched){
				this.matched = matched;
			}

			public Predicate<BlockState> getPred(){
				return this.matched;
			}
		}
	}
}

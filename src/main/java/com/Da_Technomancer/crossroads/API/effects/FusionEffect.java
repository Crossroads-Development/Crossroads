package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.fluids.ModFluids;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FusionEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, double mult){
		Block block = worldIn.getBlockState(pos).getBlock();
		if(block == Blocks.WATER){
			worldIn.setBlockState(pos, Blocks.ICE.getDefaultState());
		}else if(block == Blocks.SNOW){
			worldIn.setBlockState(pos, Blocks.ICE.getDefaultState());
		}else if(block == ModFluids.distilledWater && mult >= 4){
			worldIn.setBlockState(pos, Blocks.PACKED_ICE.getDefaultState());
		}else if(block == Blocks.SAND && mult >= 16){
			worldIn.setBlockState(pos, ModBlocks.blockPureQuartz.getDefaultState());
		}else if(block == Blocks.NETHERRACK && mult >= 8){
			worldIn.setBlockState(pos, Blocks.NETHER_BRICK.getDefaultState());
		}else if(block == Blocks.GRAVEL){
			worldIn.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
		}else if(block == Blocks.COBBLESTONE){
			worldIn.setBlockState(pos, Blocks.STONE.getDefaultState());
		}else if(block == Blocks.STONE && worldIn.getBlockState(pos).getValue(BlockStone.VARIANT) == BlockStone.EnumType.STONE){
			worldIn.setBlockState(pos, Blocks.STONEBRICK.getDefaultState());
		}else if(block == Blocks.COAL_BLOCK && mult >= 128){
			worldIn.setBlockState(pos, Blocks.DIAMOND_BLOCK.getDefaultState());
		}
	}

	public static class VoidFusionEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, double mult){
			Block block = worldIn.getBlockState(pos).getBlock();
			if(block == Blocks.ICE){
				worldIn.setBlockState(pos, Blocks.WATER.getDefaultState());
			}else if(block == Blocks.PACKED_ICE && mult >= 4){
				worldIn.setBlockState(pos, ModFluids.distilledWater.getDefaultState());
			}else if(block == ModBlocks.blockPureQuartz && mult >= 16){
				worldIn.setBlockState(pos, Blocks.SAND.getDefaultState());
			}else if(block == Blocks.NETHER_BRICK && mult >= 8){
				worldIn.setBlockState(pos, Blocks.NETHERRACK.getDefaultState());
			}else if(block == Blocks.COBBLESTONE){
				worldIn.setBlockState(pos, Blocks.GRAVEL.getDefaultState());
			}else if(block == Blocks.STONE && worldIn.getBlockState(pos).getValue(BlockStone.VARIANT) == BlockStone.EnumType.STONE){
				worldIn.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
			}else if(block == Blocks.STONEBRICK){
				worldIn.setBlockState(pos, Blocks.STONE.getDefaultState());
			}else if(block == Blocks.DIAMOND_BLOCK && mult >= 128){
				worldIn.setBlockState(pos, Blocks.COAL_BLOCK.getDefaultState());
			}
		}
	}
}

package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LightEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, int mult, Direction dir){
		BlockState state = worldIn.getBlockState(pos);
		BlockPos offsetPos;
		if(state.getBlock() == CrossroadsBlocks.blockPureQuartz){
			worldIn.setBlockState(pos, CrossroadsBlocks.blockLuminescentQuartz.getDefaultState());
		}else if(state.getMaterial() == Material.ROCK && state.getBlock() != CrossroadsBlocks.blockLuminescentQuartz && !BeamManager.solidToBeams(state, worldIn, pos)){
			worldIn.setBlockState(pos, Blocks.GLOWSTONE.getDefaultState());
		}else if(state.getMaterial() == Material.GLASS && state.getBlock() != Blocks.GLOWSTONE && state.getBlock() != CrossroadsBlocks.lightCluster && !BeamManager.solidToBeams(state, worldIn, pos)){
			worldIn.setBlockState(pos, Blocks.SEA_LANTERN.getDefaultState());
		}else if(state.getBlock().isAir(state, worldIn, pos)){
			worldIn.setBlockState(pos, CrossroadsBlocks.lightCluster.getDefaultState());
		}else if(dir != null && state.getBlock() != Blocks.GLOWSTONE && state.getBlock() != CrossroadsBlocks.blockLuminescentQuartz && state.getBlock() != Blocks.SEA_LANTERN && (state = worldIn.getBlockState(offsetPos = pos.offset(dir))).getBlock().isAir(state, worldIn, offsetPos)){
			worldIn.setBlockState(offsetPos, CrossroadsBlocks.lightCluster.getDefaultState());
		}
	}

	public static class VoidLightEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, int mult, Direction dir){
			BlockState state = worldIn.getBlockState(pos);
			if(state.getBlock() == Blocks.GLOWSTONE){
				worldIn.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
			}else if(state.getBlock() == Blocks.SEA_LANTERN){
				worldIn.setBlockState(pos, Blocks.GLASS.getDefaultState());
			}else if(state.getBlock() == CrossroadsBlocks.blockLuminescentQuartz){
				worldIn.setBlockState(pos, CrossroadsBlocks.blockPureQuartz.getDefaultState());
			}else if(state.getBlock() == CrossroadsBlocks.lightCluster){
				worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
			}
		}
	}
}
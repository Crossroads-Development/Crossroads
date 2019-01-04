package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.beams.IBeamTransparent;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LightEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, int mult, EnumFacing dir){
		IBlockState state = worldIn.getBlockState(pos);
		BlockPos offsetPos;
		if(state.getBlock() == ModBlocks.blockPureQuartz && !(state.getBlock() instanceof IBeamTransparent)){
			worldIn.setBlockState(pos, ModBlocks.blockLuminescentQuartz.getDefaultState());
		}else if(state.getMaterial() == Material.ROCK && state.getBlock() != ModBlocks.blockLuminescentQuartz && !(state.getBlock() instanceof IBeamTransparent)){
			worldIn.setBlockState(pos, Blocks.GLOWSTONE.getDefaultState());
		}else if(state.getMaterial() == Material.GLASS && state.getBlock() != Blocks.GLOWSTONE && state.getBlock() != ModBlocks.lightCluster && !(state.getBlock() instanceof IBeamTransparent)){
			worldIn.setBlockState(pos, Blocks.SEA_LANTERN.getDefaultState());
		}else if(state.getBlock().isReplaceable(worldIn, pos) && !(state.getBlock() instanceof IBeamTransparent)){
			worldIn.destroyBlock(pos, true);
			worldIn.setBlockState(pos, ModBlocks.lightCluster.getDefaultState());
		}else if(dir != null && (state = worldIn.getBlockState(offsetPos = pos.offset(dir))).getBlock().isReplaceable(worldIn, offsetPos) && state.getBlock() != ModBlocks.lightCluster && !(state.getBlock() instanceof IBeamTransparent)){
			worldIn.destroyBlock(offsetPos, true);
			worldIn.setBlockState(offsetPos, ModBlocks.lightCluster.getDefaultState());
		}
	}

	public static class VoidLightEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, int mult, EnumFacing dir){
			IBlockState state = worldIn.getBlockState(pos);
			if(state.getBlock() == Blocks.GLOWSTONE){
				worldIn.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
			}else if(state.getBlock() == Blocks.SEA_LANTERN){
				worldIn.setBlockState(pos, Blocks.GLASS.getDefaultState());
			}else if(state.getBlock() == ModBlocks.blockLuminescentQuartz){
				worldIn.setBlockState(pos, ModBlocks.blockPureQuartz.getDefaultState());
			}else if(state.getBlock() == ModBlocks.lightCluster){
				worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
			}
		}
	}
}
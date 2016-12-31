package com.Da_Technomancer.crossroads.API.effects;

import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GrowEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, double mult){
		for(int i = 0; i < mult; i++){
			IBlockState state = worldIn.getBlockState(pos);
			if(!(state.getBlock() instanceof IGrowable)){
				return;
			}
			IGrowable igrowable = (IGrowable) state.getBlock();
			if(igrowable.canGrow(worldIn, pos, state, false)){
				igrowable.grow(worldIn, worldIn.rand, pos, state);
			}
		}
	}
	
	public static class PlantKillEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, double mult){
			if(worldIn.getBlockState(pos).getBlock() instanceof IGrowable && worldIn.getBlockState(pos).getBlock() != Blocks.DEADBUSH){
				worldIn.setBlockState(pos, Blocks.DEADBUSH.getDefaultState());
			}
		}
	}
}

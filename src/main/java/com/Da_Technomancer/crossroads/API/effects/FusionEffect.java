package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.items.crafting.BeamTransmute;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FusionEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, int mult, EnumFacing dir){
		IBlockState state = worldIn.getBlockState(pos);

		BeamTransmute goal = RecipeHolder.fusionBeamRecipes.get(state);
		if(goal != null && goal.minPower <= mult){
			worldIn.setBlockState(pos, goal.state);
		}
	}

	public static class VoidFusionEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, int mult, EnumFacing dir){
			IBlockState state = worldIn.getBlockState(pos);

			BeamTransmute goal = RecipeHolder.vFusionBeamRecipes.get(state);
			if(goal != null && goal.minPower <= mult){
				worldIn.setBlockState(pos, goal.state);
			}
		}
	}
}

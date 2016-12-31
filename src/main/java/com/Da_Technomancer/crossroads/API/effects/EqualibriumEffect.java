package com.Da_Technomancer.crossroads.API.effects;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EqualibriumEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, double mult){
		//Effect is in crystal master axis
	}
	
	public static class VoidEqualibriumEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, double mult){
			//Effect is in crystal master axis.
		}
	}
}

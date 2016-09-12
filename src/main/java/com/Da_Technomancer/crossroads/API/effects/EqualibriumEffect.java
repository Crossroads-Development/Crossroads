package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.rotary.ITileMasterAxis;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EqualibriumEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, double mult){
		if(worldIn.getTileEntity(pos) instanceof ITileMasterAxis){
			((ITileMasterAxis) worldIn.getTileEntity(pos)).addTimer((int) mult);
		}
	}
	
	public static class VoidEqualibriumEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, double mult){
			if(worldIn.getTileEntity(pos) instanceof ITileMasterAxis){
				((ITileMasterAxis) worldIn.getTileEntity(pos)).addTimer((int) -mult);
			}
		}
	}
}

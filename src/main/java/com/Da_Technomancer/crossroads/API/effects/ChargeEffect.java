package com.Da_Technomancer.crossroads.API.effects;

import net.minecraft.block.material.Material;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChargeEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, double mult){
		worldIn.addWeatherEffect(new EntityLightningBolt(worldIn, pos.getX(), pos.getY(), pos.getZ(), false));
		
		if(worldIn.getBlockState(pos).getMaterial() == Material.ROCK){
			worldIn.setBlockState(pos, Blocks.REDSTONE_BLOCK.getDefaultState());
		}
	}
	
	public static class VoidChargeEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, double mult){
			if(worldIn.getBlockState(pos).getBlock() == Blocks.REDSTONE_BLOCK){
				worldIn.setBlockState(pos, Blocks.STONE.getDefaultState());
			}
		}
	}
}

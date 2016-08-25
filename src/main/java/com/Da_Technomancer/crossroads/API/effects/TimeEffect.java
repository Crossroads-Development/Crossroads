package com.Da_Technomancer.crossroads.API.effects;

import java.util.Random;

import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TimeEffect implements IEffect{

	private final Random rand = new Random();
	
	@Override
	public void doEffect(World worldIn, BlockPos pos){
		if(worldIn.getTileEntity(pos) instanceof ITickable){
			for(int i = rand.nextInt(10); i < 10; i++){
				((ITickable) worldIn.getTileEntity(pos)).update();
			}
		}
		
		if(worldIn.getBlockState(pos) != null){
			for(int i = rand.nextInt(10); i < 10; i++){
				worldIn.getBlockState(pos).getBlock().randomTick(worldIn, pos, worldIn.getBlockState(pos), rand);
			}
		}
	}
}

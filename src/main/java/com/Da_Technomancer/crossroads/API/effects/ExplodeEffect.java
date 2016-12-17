package com.Da_Technomancer.crossroads.API.effects;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ExplodeEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, double mult){
		if(!worldIn.isRemote){
			worldIn.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), (int) Math.ceil(mult / 3D), true);
		}
	}

}

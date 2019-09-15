package com.Da_Technomancer.crossroads.API.effects;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ExplodeEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, int mult, Direction dir){
		worldIn.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), (int) Math.min(Math.ceil(mult / 4D), 16), true);
	}
}

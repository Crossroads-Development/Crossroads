package com.Da_Technomancer.crossroads.API.heat.overheatEffects;

import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SlimeEffect implements OverheatEffect{

	@Override
	public void onOverheat(World worldIn, BlockPos pos){
		if(worldIn.isRemote){
			return;
		}
		worldIn.setBlockToAir(pos);

		EntitySlime slime = new EntitySlime(worldIn);
		slime.setPosition(pos.getX(), pos.getY(), pos.getZ());
		worldIn.spawnEntityInWorld(slime);
	}

}

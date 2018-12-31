package com.Da_Technomancer.crossroads.API.effects;

import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SlimeEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, int mult, EnumFacing dir){
		if(worldIn.isRemote){
			return;
		}
		worldIn.setBlockToAir(pos);

		EntitySlime slime = new EntitySlime(worldIn);
		slime.setPosition(pos.getX(), pos.getY(), pos.getZ());
		worldIn.spawnEntity(slime);
	}
}

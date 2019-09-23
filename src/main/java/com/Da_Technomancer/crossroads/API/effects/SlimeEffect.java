package com.Da_Technomancer.crossroads.API.effects;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SlimeEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, int mult, Direction dir){
		if(worldIn.isRemote){
			return;
		}
		worldIn.destroyBlock(pos, false);

		SlimeEntity slime = EntityType.SLIME.create(worldIn);
		slime.setPosition(pos.getX(), pos.getY(), pos.getZ());
		worldIn.addEntity(slime);
	}
}

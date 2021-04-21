package com.Da_Technomancer.crossroads.API.effects;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SlimeEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos){
		if(worldIn.isClientSide){
			return;
		}
		worldIn.destroyBlock(pos, false);

		SlimeEntity slime = EntityType.SLIME.create(worldIn);
		slime.setPos(pos.getX(), pos.getY(), pos.getZ());
		worldIn.addFreshEntity(slime);
	}
}

package com.Da_Technomancer.crossroads.API.effects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;

public class SlimeEffect implements IEffect{

	@Override
	public void doEffect(Level worldIn, BlockPos pos){
		if(worldIn.isClientSide){
			return;
		}
		worldIn.destroyBlock(pos, false);

		Slime slime = EntityType.SLIME.create(worldIn);
		slime.setPos(pos.getX(), pos.getY(), pos.getZ());
		worldIn.addFreshEntity(slime);
	}
}

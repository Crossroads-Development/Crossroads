package com.Da_Technomancer.crossroads.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityNitro extends EntityThrowable{

	public EntityNitro(World worldIn){
		super(worldIn);
	}

	public EntityNitro(World worldIn, EntityLivingBase throwerIn){
		super(worldIn, throwerIn);
	}

	@Override
	protected void onImpact(RayTraceResult result){
		if(!world.isRemote){
			if(result.getBlockPos() != null || result.entityHit != null){
				BlockPos targetPos = result.getBlockPos() == null ? result.entityHit.getPosition() : result.getBlockPos();
				world.createExplosion(null, targetPos.getX(), targetPos.getY(), targetPos.getZ(), 5F, true);
			}
			world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 0.5F, 0.4F / (rand.nextFloat() * 0.4F + 0.8F));
			world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.NEUTRAL, 1F, 0.4F / (rand.nextFloat() * 0.4F + 0.8F));
			world.setEntityState(this, (byte) 3);
			setDead();
		}
	}
}

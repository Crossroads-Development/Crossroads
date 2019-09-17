package com.Da_Technomancer.crossroads.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityBullet extends ThrowableEntity{

	private int damage;

	public EntityBullet(World worldIn){
		super(worldIn);
	}

	public EntityBullet(World worldIn, LivingEntity throwerIn, int damage){
		super(worldIn, throwerIn);
		this.damage = damage;
	}

	@Override
	protected void onImpact(RayTraceResult result){
		if(!world.isRemote){
			if(result.entityHit != null){
				result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), (float) damage);
			}
			world.setEntityState(this, (byte) 3);
			setDead();
		}
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt){
		super.readEntityFromNBT(nbt);
		damage = nbt.getInt("damage");
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt){
		super.writeEntityToNBT(nbt);
		nbt.putInt("damage", damage);
	}
}

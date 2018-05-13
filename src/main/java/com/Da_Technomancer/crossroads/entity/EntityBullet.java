package com.Da_Technomancer.crossroads.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityBullet extends EntityThrowable{

	private int damage;

	public EntityBullet(World worldIn){
		super(worldIn);
	}

	public EntityBullet(World worldIn, EntityLivingBase throwerIn, int damage){
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
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		damage = nbt.getInteger("damage");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setInteger("damage", damage);
	}
}

package com.Da_Technomancer.crossroads.entity;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.effects.IEffect;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityBullet extends EntityThrowable{

	private int damage;
	private MagicUnit mag;

	public EntityBullet(World worldIn){
		super(worldIn);
	}

	public EntityBullet(World worldIn, EntityLivingBase throwerIn, int damage, @Nullable MagicUnit mag){
		super(worldIn, throwerIn);
		this.damage = damage;
		this.mag = mag;
	}

	@Override
	protected void onImpact(RayTraceResult result){
		if(!world.isRemote){
			if(result.entityHit != null){
				result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), (float) damage);
			}
			if(mag != null && (result.getBlockPos() != null || result.entityHit != null)){
				IEffect effect = MagicElements.getElement(mag).getMixEffect(mag.getRGB());
				if(effect != null){
					effect.doEffect(world, result.getBlockPos() == null ? result.entityHit.getPosition() : result.getBlockPos(), mag.getPower());
				}
			}
			world.setEntityState(this, (byte) 3);
			setDead();
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		damage = nbt.getInteger("damage");
		mag = MagicUnit.loadNBT(nbt, "mag");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setInteger("damage", damage);
		if(mag != null){
			mag.setNBT(nbt, "mag");
		}
	}
}

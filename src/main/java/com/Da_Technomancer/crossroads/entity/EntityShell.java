package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.API.alchemy.AlchemyUtil;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityShell extends ThrowableEntity{

	private ReagentMap contents;
	private double temp;

	public EntityShell(World worldIn){
		super(worldIn);
	}

	public EntityShell(World worldIn, ReagentMap contents, double temp){
		super(worldIn);
		this.contents = contents;
		this.temp = temp;
	}

	public EntityShell(World worldIn, LivingEntity throwerIn, ReagentMap contents, double temp){
		super(worldIn, throwerIn);
		this.contents = contents;
		this.temp = temp;
	}

	@Override
	protected void onImpact(RayTraceResult result){
		if(!world.isRemote){
			if(contents != null){
				AlchemyUtil.releaseChemical(world, result.getBlockPos() == null ? new BlockPos(result.hitVec.x, result.hitVec.y, result.hitVec.z) : result.getBlockPos(), contents);
			}
			world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 0.5F, 0.4F / (rand.nextFloat() * 0.4F + 0.8F));
			world.setEntityState(this, (byte) 3);
			setDead();
		}
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt){
		super.readEntityFromNBT(nbt);
		temp = nbt.getDouble("temp");

		contents = ReagentMap.readFromNBT(nbt);
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt){
		super.writeEntityToNBT(nbt);
		nbt.putDouble("temp", temp);

		if(contents != null){
			contents.writeToNBT(nbt);
		}
	}
}

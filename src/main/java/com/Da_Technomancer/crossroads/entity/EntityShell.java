package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.API.alchemy.AlchemyUtil;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityShell extends EntityThrowable{

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

	public EntityShell(World worldIn, EntityLivingBase throwerIn, ReagentMap contents, double temp){
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
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		temp = nbt.getDouble("temp");

		contents = ReagentMap.readFromNBT(nbt);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setDouble("temp", temp);

		if(contents != null){
			contents.writeToNBT(nbt);
		}
	}
}

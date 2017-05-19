package com.Da_Technomancer.crossroads.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityArmRidable extends Entity{

	public EntityArmRidable(World worldIn){
		super(worldIn);
		setSize(.01F, .01F);
		setNoGravity(true);
	}

	@Override
	protected void entityInit(){
		
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound){
		
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound){
		
	}
}

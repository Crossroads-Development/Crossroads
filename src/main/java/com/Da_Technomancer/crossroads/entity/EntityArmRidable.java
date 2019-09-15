package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.tileentities.technomancy.MechanicalArmTileEntity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityArmRidable extends Entity{

	public EntityArmRidable(World worldIn){
		super(worldIn);
		setSize(.01F, .01F);
		setNoGravity(true);
	}

	private BlockPos ownerPos;

	public void setOwnerPos(BlockPos ownerPosIn){
		ownerPos = ownerPosIn;
	}

	public BlockPos getOwnerPos(){
		return ownerPos;
	}

	@Override
	protected void entityInit(){

	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt){
		ownerPos = BlockPos.fromLong(nbt.getLong("owner_pos"));
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt){
		if(ownerPos != null){
			nbt.setLong("owner_pos", ownerPos.toLong());
		}
	}

	private int ticksExisted = 0;

	@Override
	public void onUpdate(){
		if(!world.isRemote && ticksExisted++ % 200 == 1){
			if(ownerPos == null){
				setDead();
				return;
			}
			TileEntity te = world.getTileEntity(ownerPos);
			if(!(te instanceof MechanicalArmTileEntity) || !((MechanicalArmTileEntity) te).isRidable(this)){
				setDead();
				return;
			}
		}

		super.onUpdate();
	}

	@Override
	public boolean canTriggerWalking(){
		return false;
	}
}

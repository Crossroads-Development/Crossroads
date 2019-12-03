package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MechanicalArmTileEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class EntityArmRidable extends Entity{

	@ObjectHolder("arm_ridable")
	public static EntityType<EntityArmRidable> type = null;

	public EntityArmRidable(EntityType<EntityArmRidable> type, World worldIn){
		super(type, worldIn);
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
	public void readAdditional(CompoundNBT nbt){
		ownerPos = BlockPos.fromLong(nbt.getLong("owner_pos"));
	}

	@Override
	public void writeAdditional(CompoundNBT nbt){
		if(ownerPos != null){
			nbt.putLong("owner_pos", ownerPos.toLong());
		}
	}

	@Override
	public IPacket<?> createSpawnPacket(){
		return new SSpawnObjectPacket(this);
	}

	private int ticksExisted = 0;

	@Override
	protected void registerData(){
		//
	}

	@Override
	public void tick(){
		if(!world.isRemote && ticksExisted++ % 200 == 1){
			if(ownerPos == null){
				remove();
				return;
			}
			TileEntity te = world.getTileEntity(ownerPos);
			if(!(te instanceof MechanicalArmTileEntity) || !((MechanicalArmTileEntity) te).isRidable(this)){
				remove();
				return;
			}
		}

		super.tick();
	}

	@Override
	public boolean canTriggerWalking(){
		return false;
	}
}

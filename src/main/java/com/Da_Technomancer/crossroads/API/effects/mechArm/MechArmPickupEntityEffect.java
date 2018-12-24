package com.Da_Technomancer.crossroads.API.effects.mechArm;

import java.util.List;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.entity.EntityArmRidable;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MechanicalArmTileEntity;
import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MechArmPickupEntityEffect implements IMechArmEffect{

	@Override
	public boolean onTriggered(World world, BlockPos pos, double posX, double posY, double posZ, EnumFacing side, EntityArmRidable ent, MechanicalArmTileEntity te){
		if(ent.getPassengers().size() == 0){
			List<Entity> ents = world.getEntitiesInAABBexcluding(ent, ent.getEntityBoundingBox().grow(0.5D), CAN_GRAB);
			if(ents.size() != 0){
				ents.get(0).startRiding(ent, false);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean useSideModifier(){
		return false;
	}

	private static final Predicate<Entity> CAN_GRAB = new Predicate<Entity>(){
		@Override
		public boolean apply(@Nullable Entity input){
			return input != null && input.isEntityAlive() && !(input instanceof EntityArmRidable);
		}
	};
}
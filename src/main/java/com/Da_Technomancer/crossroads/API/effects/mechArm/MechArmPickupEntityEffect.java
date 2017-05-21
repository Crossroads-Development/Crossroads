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
		System.out.println(posX + ", " + posY + ", " + posZ);//TODO for debugging
		if(ent.getPassengers().size() == 0){
			System.out.println("NO PASSENGERS");//TODO for debugging
			List<Entity> ents = world.getEntitiesInAABBexcluding(ent, ent.getEntityBoundingBox().expand(.5D, .5D, .5D), CAN_GRAB);
			if(ents != null && ents.size() != 0){
				ents.get(0).startRiding(ent, true);
				return true;
			}
		}else{
			System.out.println(ent.getPassengers().get(0).getName());//TODO for debugging
		}
		return false;
	}
	
	private static final Predicate<Entity> CAN_GRAB = new Predicate<Entity>(){
		@Override
		public boolean apply(@Nullable Entity input){
			return input != null && input.isEntityAlive() && !(input instanceof EntityArmRidable);
		}
	};
}
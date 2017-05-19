package com.Da_Technomancer.crossroads.API.effects.mechArm;

import java.util.List;

import com.Da_Technomancer.crossroads.entity.EntityArmRidable;

import net.minecraft.entity.Entity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MechArmPickupEntityEffect implements IMechArmEffect{

	@Override
	public boolean onTriggered(World world, BlockPos pos, double posX, double posY, double posZ, EnumFacing side, EntityArmRidable ent){
		if(ent.getPassengers().size() == 0){
			List<Entity> ents = world.getEntitiesInAABBexcluding(ent, ent.getEntityBoundingBox().expand(.5D, .5D, .5D), EntitySelectors.IS_ALIVE);
			if(ents != null && ents.size() != 0){
				ents.get(0).startRiding(ent, true);
				return true;
			}
		}
		return false;
	}
}
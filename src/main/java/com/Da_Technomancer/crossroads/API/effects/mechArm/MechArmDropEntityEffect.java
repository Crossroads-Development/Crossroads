package com.Da_Technomancer.crossroads.API.effects.mechArm;

import com.Da_Technomancer.crossroads.entity.EntityArmRidable;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MechanicalArmTileEntity;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MechArmDropEntityEffect implements IMechArmEffect{

	@Override
	public boolean onTriggered(World world, BlockPos pos, double posX, double posY, double posZ, EnumFacing side, EntityArmRidable ent, MechanicalArmTileEntity te){
		if(ent.getPassengers().size() != 0){
			Entity passenger = ent.getPassengers().get(0);
			passenger.dismountRidingEntity();
			passenger.setVelocity(0, 0, 0);
			return true;
		}
		return false;
	}

	@Override
	public boolean useSideModifier(){
		return false;
	}
}
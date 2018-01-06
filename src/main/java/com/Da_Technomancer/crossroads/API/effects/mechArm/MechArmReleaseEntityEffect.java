package com.Da_Technomancer.crossroads.API.effects.mechArm;

import com.Da_Technomancer.crossroads.entity.EntityArmRidable;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MechanicalArmTileEntity;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MechArmReleaseEntityEffect implements IMechArmEffect{

	@Override
	public boolean onTriggered(World world, BlockPos pos, double posX, double posY, double posZ, EnumFacing side, EntityArmRidable ent, MechanicalArmTileEntity te){
		if(ent.getPassengers().size() != 0){
			Entity passenger = ent.getPassengers().get(0);
			passenger.dismountRidingEntity();

			//The principle used here is to calculate the previous arm position, draw a line between the start and end points, and launch the entity along the direction of that line with velocity equal to that necessary to move across that line in 1 tick. 
			double lengthCross = Math.sqrt(Math.pow(MechanicalArmTileEntity.LOWER_ARM_LENGTH, 2) + Math.pow(MechanicalArmTileEntity.UPPER_ARM_LENGTH, 2) - (2D * MechanicalArmTileEntity.LOWER_ARM_LENGTH * MechanicalArmTileEntity.UPPER_ARM_LENGTH * Math.cos(te.angleRecord[2])));
			double thetaD = te.angleRecord[1] + te.angleRecord[2] + Math.asin(Math.sin(te.angleRecord[2]) * MechanicalArmTileEntity.LOWER_ARM_LENGTH / lengthCross);
			double holder = -Math.cos(thetaD) * lengthCross;

			double oldPosX = (holder * Math.cos(te.angleRecord[0])) + .5D + (double) te.getPos().getX();
			double oldPosY = (-Math.sin(thetaD) * lengthCross) + 1D + (double) te.getPos().getY();
			double oldPosZ = (holder * Math.sin(te.angleRecord[0])) + .5D + (double) te.getPos().getZ();

			passenger.motionX = posX - oldPosX;
			passenger.motionY = posY - oldPosY;
			passenger.motionZ = posZ - oldPosZ;
			passenger.velocityChanged = true;
			return true;
		}
		return false;
	}
}
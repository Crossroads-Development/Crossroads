package com.Da_Technomancer.crossroads.API.effects.mechArm;

import javax.annotation.Nonnull;

import com.Da_Technomancer.crossroads.entity.EntityArmRidable;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMechArmEffect{
	
	/**
	 * Call on the SERVER SIDE ONLY.
	 * @param world The World of the end. 
	 * @param pos The BlockPos of the end.
	 * @param posX The exact X coordinate of the end.
	 * @param posY The exact Y coordinate of the end.
	 * @param posZ The exact Z coordinate of the end.
	 * @param side The signal specified EnumFacing to effect.
	 * @param ent The EntityArmRidable that the final entity would ride.
	 * @return Whether this did anything. 
	 */
	public boolean onTriggered(World world, BlockPos pos, double posX, double posY, double posZ, EnumFacing side, @Nonnull EntityArmRidable ent);

}
package com.Da_Technomancer.crossroads.API.effects.mechArm;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.entity.EntityArmRidable;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MechanicalArmTileEntity;

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
	 * @param te The TileEntity calling this method.
	 * @return Whether this did anything. 
	 */
	public boolean onTriggered(World world, BlockPos pos, double posX, double posY, double posZ, @Nullable EnumFacing side, @Nonnull EntityArmRidable ent, MechanicalArmTileEntity te);

	/**
	 * @return Whether there is any possibility that onTriggered will look at the side argument. Used for optimization
	 */
	public boolean useSideModifier();

}
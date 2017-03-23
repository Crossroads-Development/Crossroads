package com.Da_Technomancer.crossroads.API;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.Main;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;

/**This class is for holding operations that I use often.*/
@SuppressWarnings("deprecation")
public final class MiscOp{

	public static double betterRound(double numIn, int decPlac){
		double opOn = Math.round(numIn * Math.pow(10, decPlac)) / Math.pow(10D, decPlac);
		return opOn;
	}

	/**
	 * An alternate version of Math.round. Instead of
	 * hitting ints, it uses the secong arg ex. tiers = 1 is like round, tiers =
	 * 2 means goes to closest .5 value, etc.
	 */
	public static double tiersRound(double numIn, int tiers){
		return ((double) Math.round(numIn * (double) tiers)) / (double) tiers;
	}

	public static double findEfficiency(double speedIn, double lowerLimit, double upperLimit){
		speedIn = Math.abs(speedIn);
		return speedIn < lowerLimit ? 0 : (speedIn >= upperLimit ? 1 : (speedIn - lowerLimit) / (upperLimit - lowerLimit));
	}

	public static int posOrNeg(int in){
		return posOrNeg(in, 0);
	}
	
	public static int posOrNeg(int in, int zeroCase){
		return in == 0 ? zeroCase : (in < 0 ? -1 : 1);
	}

	public static double posOrNeg(double in){
		return posOrNeg(in, 0);
	}
	
	public static double posOrNeg(double in, double zeroCase){
		return in == 0 ? zeroCase : (in < 0 ? -1 : 1);
	}
	
	/**
	 * The same as Math.round except if the decimal
	 * is exactly .5 then it rounds down.
	 * 
	 * This is for systems that require rounding and
	 * NEED the distribution of output to not be higher than
	 * the input to prevent dupe bugs.
	 */
	public static int safeRound(double in){
		if(in % 1 <= .5D){
			return (int) Math.floor(in);
		}else{
			return (int) Math.ceil(in);
		}
	}

	public static NBTTagCompound getPlayerTag(EntityPlayer playerIn){
		NBTTagCompound tag = playerIn.getEntityData();
		if(!tag.hasKey(EntityPlayer.PERSISTED_NBT_TAG)){
			tag.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
		}
		tag = tag.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
	
		if(!tag.hasKey(Main.MODID)){
			tag.setTag(Main.MODID, new NBTTagCompound());
		}
	
		return tag.getCompoundTag(Main.MODID);
	}
	
	/**
	 * For finding which box within a block is being moused over. Used for example by gear breaking.
	 */
	@Nullable
	public static AxisAlignedBB rayTraceMulti(ArrayList<AxisAlignedBB> boxes, Vec3d start, Vec3d end){
		if(boxes == null || boxes.size() == 0){
			return null;
		}
		
		float dist = 0;
		AxisAlignedBB closest = null;
		
		for(AxisAlignedBB box : boxes){
			RayTraceResult raytraceresult = box.calculateIntercept(start, end);
			if(raytraceresult != null && (dist > raytraceresult.hitVec.subtract(start).lengthVector() || dist == 0)){
				dist = (float) raytraceresult.hitVec.subtract(start).lengthVector();
				closest = box;
			}
		}

		return closest;
	}

	/**
	 * A server-side friendly version of {@link Entity#rayTrace(double, float)}
	 * 
	 */
	@Nullable
	public static RayTraceResult rayTrace(Entity ent, double blockReachDistance){
		Vec3d vec3d = ent.getPositionVector().add(new Vec3d(0, ent.getEyeHeight(), 0));
		Vec3d vec3d1 = ent.getLook(1F);
		Vec3d vec3d2 = vec3d.addVector(vec3d1.xCoord * blockReachDistance, vec3d1.yCoord * blockReachDistance, vec3d1.zCoord * blockReachDistance);
		return ent.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
	}
	
	/**
	 * Localizes the passed String. Works on the server side.
	 */
	public static String localizeEither(String toLocal){
		return I18n.translateToLocalFormatted(toLocal);
	}
	
	/**
	 * @param world
	 * @param pos
	 * @param cap
	 * @param side
	 * @return Whether or not there is A) A TileEntity at pos and B) The TileEntity has Capability cap on side
	 */
	public static boolean safeHasCap(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Capability<?> cap, EnumFacing side){
		return world.getTileEntity(pos) != null && world.getTileEntity(pos).hasCapability(cap, side);
	}

	/**
	 * Returns a long that contains the chunk's coordinates (In chunk coordinates). Suitable for HashMap keys. 
	 * It should be noted that this is NOT the same as {@link ChunkPos#asLong(int, int)} in terms of results. 
	 */
	public static long getLongFromChunk(@Nonnull Chunk chunk){
		return (((long) chunk.xPosition) << 32) | (chunk.zPosition & 0xffffffffL);
	}
	
	public static ChunkPos getChunkPosFromLong(long combinedCoord){
		return new ChunkPos((int) (combinedCoord >> 32), (int) combinedCoord);
	}

	/**
	 * @returns The coordinate in chunk relative form. NOT the same as coord % 16. Note that this value should be divided by 2 for use with fieldNodes and nodeForces.
	 */
	public static int getChunkRelativeCoord(int coord){
		return coord - (16 * Math.floorDiv(coord, 16));
	}
	
	/**
	 * Returns a long that contains the chunk's coordinates (In chunk coordinates). Suitable for HashMap keys. 
	 * It should be noted that this is NOT the same as {@link ChunkPos#asLong(int, int)} in terms of results. 
	 */
	public static long getLongFromChunkPos(@Nonnull ChunkPos pos){
		return (((long) pos.chunkXPos << 32) | (pos.chunkZPos & 0xffffffffL));
	}
}

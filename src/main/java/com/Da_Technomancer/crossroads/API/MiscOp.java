package com.Da_Technomancer.crossroads.API;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.Main;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.oredict.OreDictionary;

/**This class is for holding operations that are used often.*/
public final class MiscOp{

	public static double betterRound(double numIn, int decPlac){
		return Math.round(numIn * Math.pow(10, decPlac)) / Math.pow(10D, decPlac);
	}

	public static double findEfficiency(double speedIn, double lowerLimit, double upperLimit){
		speedIn = Math.abs(speedIn);
		return speedIn < lowerLimit ? 0 : (speedIn >= upperLimit ? 1 : (speedIn - lowerLimit) / (upperLimit - lowerLimit));
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

	/**
	 * Call on server side only.
	 * @param playerIn The player whose tag is being retrieved.
	 * @return The player's persistent NBT tag. Also sets a boolean for if this is multiplayer.
	 */
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
	 */
	@Nullable
	public static RayTraceResult rayTrace(Entity ent, double blockReachDistance){
		Vec3d vec3d = ent.getPositionVector().addVector(0, ent.getEyeHeight(), 0);
		Vec3d vec3d2 = vec3d.add(ent.getLook(1F).scale(blockReachDistance));
		return ent.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
	}

	/**
	 * Returns a long that contains the chunk's coordinates (In chunk coordinates). Suitable for HashMap keys. 
	 * It should be noted that this is NOT the same as {@link ChunkPos#asLong(int, int)} in terms of results. 
	 */
	public static long getLongFromChunk(@Nonnull Chunk chunk){
		return (((long) chunk.x) << 32) | (chunk.z & 0xffffffffL);
	}

	public static ChunkPos getChunkPosFromLong(long combinedCoord){
		return new ChunkPos((int) (combinedCoord >> 32), (int) combinedCoord);
	}

	/**
	 * @returns The coordinate in chunk relative form. NOT the same as coord % 16.
	 */
	public static int getChunkRelativeCoord(int coord){
		return coord - (16 * Math.floorDiv(coord, 16));
	}

	/**
	 * Returns a long that contains the chunk's coordinates (In chunk coordinates). Suitable for HashMap keys. 
	 * It should be noted that this is NOT the same as {@link ChunkPos#asLong(int, int)} in terms of results. 
	 */
	public static long getLongFromChunkPos(@Nonnull ChunkPos pos){
		return (((long) pos.x << 32) | (pos.z & 0xffffffffL));
	}

	public static ItemStack getOredictStack(String oreDict, int count){
		List<ItemStack> matches = OreDictionary.getOres(oreDict);
		ItemStack out = matches.isEmpty() ? ItemStack.EMPTY : matches.get(0).copy();
		out.setCount(count);
		return out;
	}

	public static boolean hasOreDict(ItemStack stack, String oreDict){
		for(int id : OreDictionary.getOreIDs(stack)){
			if(id == OreDictionary.getOreID(oreDict)){
				return true;
			}
		}
		return false;
	}
}

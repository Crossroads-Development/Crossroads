package com.Da_Technomancer.crossroads.API;

import java.util.ArrayList;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.Main;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

/**This class is for holding mathematical operations that I use often.*/
public final class MiscOp{

	public static double betterRound(double numIn, int decPlac){
		double opOn = Math.round(numIn * Math.pow(10, decPlac)) / Math.pow(10D, decPlac);
		return opOn;
	}

	/**
	 * a version of Math.ceil that factors in negative values better. Instead of
	 * hitting ints, it uses the secong arg ex. tiers = 1 is like ceil, tiers =
	 * 2 means goes to closest .5 value, rounding up
	 */
	public static double centerCeil(double numIn, int tiers){
		return ((numIn > 0) ? Math.ceil(numIn * tiers) : Math.floor(numIn * tiers)) / tiers;
	}

	public static double findEfficiency(double speedIn, double lowerLimit, double upperLimit){
		speedIn = Math.abs(speedIn);
		return speedIn < lowerLimit ? 0 : (speedIn >= upperLimit ? 1 : (speedIn - lowerLimit) / (upperLimit - lowerLimit));
	}

	public static int posOrNeg(int in){
		return in == 0 ? 0 : (in < 0 ? -1 : 1);
	}

	public static double posOrNeg(double in){
		return in == 0 ? 0 : (in < 0 ? -1D : 1D);
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
	
	@Nullable
	public static AxisAlignedBB rayTraceMulti(ArrayList<AxisAlignedBB> boxes, Vec3d start, Vec3d end){
		if(boxes == null || boxes.size() == 0){
			return null;
		}
		
		float dist = 0;
		AxisAlignedBB closest = null;
		
		for(AxisAlignedBB box : boxes){
			RayTraceResult raytraceresult = box.calculateIntercept(start, end);
			if(raytraceresult != null && dist < raytraceresult.hitVec.lengthSquared()){
				dist = (float) raytraceresult.hitVec.lengthSquared();
				closest = box;
			}
		}
		
		return closest;
	}
}

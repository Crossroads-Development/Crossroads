package com.Da_Technomancer.crossroads.API;

import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.Main;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

/**This class is for holding operations that I use often.*/
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
		return in == 0 ? 0 : (in < 0 ? -1 : 1);
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
		NBTTagCompound out = tag.getCompoundTag(Main.MODID);
		out.setBoolean("multiplayer", FMLCommonHandler.instance().getSide() == Side.SERVER);//The only way I could think of to check if it's multiplayer on the client side is to get it on server side and send it via packet. Feel free to replace this with a better way. 
		return out;
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

	/**
	 * A chunk being loaded and a chunk ticking are two different things; in some cases, a loaded chunk (such as a spawn chunk) might not be able to actually tick tile entities. 
	 * @param world
	 * @param pos
	 * @return Whether the chunk can tick TileEntities (does not check if the chunk actually contains any TileEntities). 
	 */
	public static boolean isChunkTicking(WorldServer world, BlockPos pos){

		if(!world.isBlockLoaded(pos, false)){
			return false;
		}

		if(world.getPersistentChunks().isEmpty() && world.playerEntities.isEmpty()){
			try{
				if(WORLD_LOADING_TIMER.getInt(world) >= 300){
					return false;
				}
			}catch(IllegalArgumentException | IllegalAccessException e){
				Main.logger.catching(e);
				return false;
			}
		}
		return true;
	}

	private static final Field WORLD_LOADING_TIMER;

	static{
		Field holder = null;
		try{
			for(Field f : WorldServer.class.getDeclaredFields()){
				if("field_80004_Q".equals(f.getName()) || "updateEntityTick".equals(f.getName())){
					holder = f;
					holder.setAccessible(true);
					break;
				}
			}
			//For no apparent reason ReflectionHelper consistently crashes in an obfus. environment for me with the normal method, so the above for loop is used instead.
		}catch(Exception e){
			Main.logger.catching(e);
		}
		WORLD_LOADING_TIMER = holder;
	}
}

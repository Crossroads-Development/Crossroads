package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.items.crafting.OreDictCraftingStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public final class MiscUtil{

	public static double betterRound(double numIn, int decPlac){
		return Math.round(numIn * Math.pow(10, decPlac)) / Math.pow(10D, decPlac);
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

	public static Predicate<ItemStack> oreDictPred(String oreDict){
		return new OreDictCraftingStack(oreDict);
	}
}

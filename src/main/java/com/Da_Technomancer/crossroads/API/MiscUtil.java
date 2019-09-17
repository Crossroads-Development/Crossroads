package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.CrossroadsConfig;
import com.Da_Technomancer.crossroads.items.crafting.OreDictCraftingStack;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
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
	public static CompoundNBT getPlayerTag(PlayerEntity playerIn){
		CompoundNBT tag = playerIn.getEntityData();
		if(!tag.hasKey(PlayerEntity.PERSISTED_NBT_TAG)){
			tag.setTag(PlayerEntity.PERSISTED_NBT_TAG, new CompoundNBT());
		}
		tag = tag.getCompoundTag(PlayerEntity.PERSISTED_NBT_TAG);

		if(!tag.hasKey(Crossroads.MODID)){
			tag.setTag(Crossroads.MODID, new CompoundNBT());
		}
		CompoundNBT out = tag.getCompoundTag(Crossroads.MODID);
		out.setBoolean("multiplayer", FMLCommonHandler.instance().getSide() == Side.SERVER);//The only way I could think of to check if it's multiplayer on the render side is to get it on server side and send it via packet. Feel free to replace this with a better way.
		return out;
	}

	/**
	 * A server-side friendly version of {@link Entity#rayTrace(double, float)}
	 */
	@Nullable
	public static RayTraceResult rayTrace(Entity ent, double blockReachDistance){
		Vec3d vec3d = ent.getPositionVector().add(0, ent.getEyeHeight(), 0);
		Vec3d vec3d2 = vec3d.add(ent.getLook(1F).scale(blockReachDistance));
		return ent.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
	}

	public static ChunkPos getChunkPosFromLong(long combinedCoord){
		return new ChunkPos((int) (combinedCoord >> 32), (int) combinedCoord);
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
		if(stack.isEmpty()){
			return false;
		}

		int goalID = OreDictionary.getOreID(oreDict);
		for(int id : OreDictionary.getOreIDs(stack)){
			if(id == goalID){
				return true;
			}
		}
		return false;
	}

	public static Predicate<ItemStack> oreDictPred(String oreDict){
		return new OreDictCraftingStack(oreDict);
	}

	public static String localize(String input){
		return new TranslationTextComponent(input).getUnformattedComponentText();
	}

	public static boolean canBreak(BlockState state, boolean client){
		String[] bannedBlocks = CrossroadsConfig.getConfigStringList(CrossroadsConfig.destroyBlacklist, client);
		String id = state.getBlock().getRegistryName().toString();
		for(String s : bannedBlocks){
			if(s.equals(id)){
				return false;
			}
		}
		return true;
	}

	@Nullable
	public static Method reflectMethod(Class clazz, String textName, String rawName){
		//TODO Change the names used by basically everything that calls this
		try{
			for(Method m : clazz.getDeclaredMethods()){
				if(textName.equals(m.getName()) || rawName.equals(m.getName())){
					m.setAccessible(true);
					return m;
				}
			}
			//For no apparent reason ReflectionHelper consistently crashes in an obfus. environment for me with the Forge method, so the above for loop is used instead.
		}catch(Exception e){
			Crossroads.logger.catching(e);
		}
		return null;
	}
}

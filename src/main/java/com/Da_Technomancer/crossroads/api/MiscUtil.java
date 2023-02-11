package com.Da_Technomancer.crossroads.api;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.api.ConfigUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;

public final class MiscUtil{

	/**
	 * A common style applied to "quip" lines in tooltips
	 */
	public static final Style TT_QUIP = ConfigUtil.TT_QUIP;

	//Useful flags for Level::setBlock
	public static final int BLOCK_FLAG_UPDATE = 1;
	public static final int BLOCK_FLAG_SYNC_TO_CLIENT = 2;
	public static final int BLOCK_FLAG_SUPPRESS_OBSERVERS = 16;
	/**
	 * For most usecases of setting blocks on the server-side
	 */
	public static final int BLOCK_FLAGS_NORMAL = BLOCK_FLAG_UPDATE | BLOCK_FLAG_SYNC_TO_CLIENT;
	/**
	 * For changing a blockstate property which is purely visual
	 */
	public static final int BLOCK_FLAGS_VISUAL = BLOCK_FLAG_SYNC_TO_CLIENT | BLOCK_FLAG_SUPPRESS_OBSERVERS;

	/**
	 * Rounds to a set number of decimal places
	 * @param numIn The value to round
	 * @param decPlac The number of decimal places to round to
	 * @return The rounded value
	 */
	public static double preciseRound(double numIn, int decPlac){
		return Math.round(numIn * Math.pow(10, decPlac)) / Math.pow(10D, decPlac);
	}

	/**
	 * The same as Math.round except if the decimal
	 * is exactly .5 then it rounds down.
	 *
	 * This is for systems that require rounding and
	 * NEED the distribution of output to not be higher than
	 * the input to prevent dupe bugs.
	 * @param in The value to round
	 * @return The rounded value
	 */
	public static int safeRound(double in){
		if(in % 1 <= .5D){
			return (int) Math.floor(in);
		}else{
			return (int) Math.ceil(in);
		}
	}

	public static float clockModulus(float a, float b){
		return ((a % b) + b) % b;
	}

	/**
	 * A server-side friendly version of Entity.class' raytrace (currently called Entity#pick(double, float, boolean))
	 */
	public static BlockHitResult rayTrace(Entity ent, double blockReachDistance){
		Vec3 vec3d = ent.position().add(0, ent.getEyeHeight(), 0);
		Vec3 vec3d2 = vec3d.add(ent.getViewVector(1F).scale(blockReachDistance));
		return ent.level.clip(new ClipContext(vec3d, vec3d2, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, ent));
	}

	/**
	 * Localizes the input. Do not trust the result if called on the physical server
	 * @param input The string to localize
	 * @return The localized string
	 */
	public static String localize(String input){
		return Component.translatable(input).getString();
	}

	/**
	 * Localizes and formats the input. Do not trust the result if called on the physical server
	 * @param input The string to localize
	 * @param formatArgs Arguments to pass the formatter
	 * @return The localized and formatted string
	 */
	public static String localize(String input, Object... formatArgs){
		return Component.translatable(input, formatArgs).getString();
	}

	public static String getLocalizedFluidName(String localizationKey){
		return localizationKey == null || localizationKey.isEmpty() || localizationKey.equals("block.minecraft.air") ? localize("tt.crossroads.boilerplate.empty") : localize(localizationKey);
	}

	/**
	 * Calculates a set of integer quantities to withdraw from a fixed integer source, where the total size of the withdrawn set is toWithdraw
	 * Does not mutate the passed arguments
	 * Attempts to distribute proportionally to source, with an over-emphasis on small amounts for the remainder
	 * Will not withdraw any variety in greater quantity than in source
	 * @param src The source quantities to calculate withdrawal from. Only positive and zero values are allowed
	 * @param toWithdraw The quantity to withdraw. Must be positive or zero
	 * @return The distribution withdrawn. Each index corresponds to the same src index
	 */
	public static int[] withdrawExact(int[] src, int toWithdraw){
		if(toWithdraw <= 0 || src.length == 0){
			//Withdraw nothing
			return new int[src.length];
		}
		int srcQty = 0;
		for(int val : src){
			srcQty += val;
		}
		if(toWithdraw < srcQty){
			int[] withdrawn = new int[src.length];
			double basePortion = (double) toWithdraw / (double) srcQty;//Multiplier for src applied get withdrawn. Any remaining space will be filled by a different distribution
			int totalWithdrawn = 0;
			for(int i = 0; i < withdrawn.length; i++){
				int toMove = (int) (basePortion * src[i]);//Intentional truncation- rounding down
				totalWithdrawn += toMove;
				withdrawn[i] += toMove;
			}

			if(totalWithdrawn < toWithdraw){
				//For the remaining space to fill, perform a round robin distribution
				//We start the distribution with the smallest (least magnitude pointed to) index, and end with the largest.
				//We do this to reduce dependence on the index order, and because it has been found that ensuring small pools are drawn from is more conveniently for play

				//Sort the source pools from least to greatest
				//Selection sort, using an array of values where each value is an index in src
				int[] sorted = new int[src.length];
				for(int i = 0; i < sorted.length - 1; i++){
					int smallestVal = Integer.MAX_VALUE;
					int smallestInd = 0;
					for(int j = i; j < sorted.length; j++){
						if(i == 0){
							//For the first pass, we populate sorted with 0,1,2,3,etc to represent the original, unsorted array
							sorted[j] = j;
						}
						//src[sorted[j]] < smallestVal is the normal sorting ordering
						//sorted[j] < sorted[smallestInd] is a secondary sorting ordering that is only considered if (src[sorted[j]] == smallestVal)
						//This secondary sorting ordering was added to maintain certain useful specialized behaviour from the previous algorithm (red-green-blue-black sorting). It may be removed in a later update
						//It does not interfere with sorting from lowest to highest quantity, and only acts as a tie-breaker
						if(src[sorted[j]] < smallestVal || sorted[j] < sorted[smallestInd] && src[sorted[j]] == smallestVal){
							smallestInd = sorted[j];
							smallestVal = src[sorted[j]];
						}
					}
					if(smallestInd != i){
						int toSwap = sorted[i];
						sorted[i] = smallestInd;
						sorted[smallestInd] = toSwap;
					}
				}

				//Perform round robin distribution
				int indexInSorted = 0;//The index in sorted (which contains more indices...) we are drawing from
				while(totalWithdrawn < toWithdraw){
					int indexInSrc = sorted[indexInSorted];
					if(src[indexInSrc] > withdrawn[indexInSrc]){//Make sure there is sufficient to withdraw from this pool, otherwise proceed to the next pool
						//Withdraw one from the current pool
						withdrawn[indexInSrc]++;
						totalWithdrawn++;
					}
					//Move to the next pool in sorted
					indexInSorted++;
					indexInSorted %= sorted.length;
				}
			}

			return withdrawn;
		}else{
			//Less total in src than to withdraw. Return same as in src
			return Arrays.copyOf(src, src.length);
		}
	}

	/**
	 * Server-side safe way of setting hunger and saturation of a player
	 * @param player The player to set the food of
	 * @param hunger New hunger value, [0, 20]
	 * @param saturation New saturation value, [0, 20]
	 */
	public static void setPlayerFood(Player player, int hunger, float saturation){
		FoodData stats = player.getFoodData();
//		CompoundTag nbt = new CompoundTag();
//		stats.addAdditionalSaveData(nbt);
//		nbt.putInt("foodLevel", Math.min(hunger, 20));
//		nbt.putFloat("foodSaturationLevel", Math.min(20F, saturation));
//		stats.readAdditionalSaveData(nbt);
		stats.setFoodLevel(Math.min(20, hunger));
		stats.setSaturation(Math.min(20F, saturation));
	}

	/**
	 * Displays a message to the player
	 * Works on both sides
	 * @param player The player to add a message to
	 * @param message The message to send
	 */
	public static void displayMessage(Player player, Component message){
		player.displayClientMessage(message, true);
	}

	/**
	 * Gets the name of a dimension, for logging purposes
	 * @param world The world to get the dimension of
	 * @return The name of the dimension, for logging purposes, unlocalized
	 */
	public static String getDimensionName(@Nonnull Level world){
		return world.dimension().location().toString();
	}

	/**
	 * Gets the registry key for a world with the given registry ID
	 * @param registryID The world registry keyname
	 * @param cache An optional cache parameter- will return this value if it matches the passed ID
	 * @return The registry key in the World Key registry associated with a given registry keyname
	 */
	public static ResourceKey<Level> getWorldKey(ResourceLocation registryID, @Nullable ResourceKey<Level> cache){
		if(cache != null && cache.location().equals(registryID)){
			return cache;
		}

		return ResourceKey.create(Registry.DIMENSION_REGISTRY, registryID);
	}

	/**
	 * Gets the world associated with a given registry key, or null if it doesn't exist
	 * Server side only
	 * @param registryKey The registry key to search for
	 * @param server The server instance
	 * @return The world instance for the passed registry key
	 */
	@Nullable
	public static ServerLevel getWorld(ResourceKey<Level> registryKey, MinecraftServer server){
		return server.getLevel(registryKey);
	}

	/**
	 * Damages an entity with lightning damage, and triggers any lightning effect on the mob. Does not summon a bolt of lightning
	 * CALL ON THE VIRTUAL SERVER SIDE ONLY
	 * @param ent The entity to strike with lightning
	 * @param damage The damage to deal. Will deal a minimum of 5 damage, regardless of value; Passing 0 to do default lightning damage is acceptable
	 * @param lightning The lightning entity doing the striking. Pass null if there is no lightning bolt entity
	 */
	public static void attackWithLightning(LivingEntity ent, float damage, @Nullable LightningBolt lightning){
		if(lightning == null){
			//Create a generic lightning entity at the entity position, but don't add it to the world
			lightning = EntityType.LIGHTNING_BOLT.create(ent.level);
			lightning.moveTo(ent.position());
		}
		ent.thunderHit((ServerLevel) ent.level, lightning);//Deals 5 lightning damage
		if(damage > 5){
			ent.hurt(DamageSource.LIGHTNING_BOLT, damage - 5F);//Deal any additional damage
		}
	}

	public static int getLight(Level world, BlockPos pos){
		return world.isThundering() ? world.getMaxLocalRawBrightness(pos, 10) : world.getMaxLocalRawBrightness(pos);
	}

	public static <T> ResourceLocation getRegistryName(T registeredObject, ResourceKey<? extends Registry<T>> registryKey){
		IForgeRegistry<T> registry = RegistryManager.ACTIVE.getRegistry(registryKey);
		if(registry == null){
			Crossroads.logger.error("Invalid registry: " + registryKey.registry());
			throw new IllegalArgumentException();
		}

		return getRegistryName(registeredObject, registry);
	}

	public static <T> ResourceLocation getRegistryName(T registeredObject, IForgeRegistry<T> registry){
		ResourceLocation result = registry.getKey(registeredObject);
		if(result == null){
			IllegalArgumentException ex = new IllegalArgumentException("Attempted to lookup unregistered object: " + registeredObject + "; in registry: " + registry.getRegistryName());
			Crossroads.logger.throwing(ex);
			throw ex;
		}
		return result;
	}

	public static <T, U> U putReturn(Map<T, U> map, T key, U val){
		map.put(key, val);
		return val;
	}
}

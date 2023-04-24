package com.Da_Technomancer.crossroads.api.witchcraft;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * For items that need special storage or they spoil after a specific amount of time
 */
public interface IPerishable{

	String SPOIL_KEY = "cr_spoil_time";

	/**
	 * Whether this item has already spoiled
	 * @param stack The stack
	 * @param world A world instance. Used to get the time
	 * @return Whether the item is spoiled. Returns false for non-perishable input item
	 */
	static boolean isSpoiled(ItemStack stack, Level world){
		if(world == null){
			return false;
		}
		long spoilTime = getAndInitSpoilTime(stack, world);
		return spoilTime >= 0 && spoilTime < world.getGameTime();
	}

	/**
	 * Gets the timestamp when this will spoil
	 * If no spoil time was set, this sets the spoil time to the default
	 * @param stack The stack, may be modified (modifications should write back to the caller)
	 * @param world A world instance. Used to get the time
	 * @return The timestamp (in ticks, in the form of a gametime) when this will spoil; -1 if this has no spoiltime set and we couldn't set one
	 */
	static long getAndInitSpoilTime(ItemStack stack, @Nullable Level world){
		if(stack.getItem() instanceof IPerishable perishable){
			CompoundTag nbt = stack.getOrCreateTag();
			//Correct broken stacks, by setting a spoil time for a fresh item
			if(!nbt.contains(SPOIL_KEY)){
				if(world != null && !world.isClientSide()){
					setSpoilTime(stack, perishable.getLifetime(), world.getGameTime());
				}else{
					return -1;
				}
			}
			return nbt.getLong(SPOIL_KEY);
		}
		return -1;
	}

	/**
	 * Sets a timestamp when the stack will spoil
	 * @param stack The stack; will be modified to match the return value
	 * @param spoilTime The desired spoil time, as a duration in ticks
	 * @param worldTime The current game time
	 * @return The modified stack
	 */
	static ItemStack setSpoilTime(ItemStack stack, long spoilTime, long worldTime){
		CompoundTag nbt = stack.getOrCreateTag();
		nbt.putLong(SPOIL_KEY, spoilTime + worldTime);
		return stack;
	}

	/**
	 * Gets the total lifetime of a fresh copy of this item
	 * @return Total lifetime, as a duration in ticks
	 */
	long getLifetime();

	/**
	 * Gets the temperature this item must be frozen below to freeze it
	 * @return The freezing temperature, in Celsius. Return a value below absolute zero to prevent freezing
	 */
	double getFreezeTemperature();

	/**
	 * Rewinds the spoil counter by the passed duration
	 * Used for cold storage, to prevent items from spoiling
	 * Items can choose to refuse to freeze based on temperature
	 * @param stack The stack being frozen. Will be modified to match the return value
	 * @param world The world. Used to get the game time
	 * @param temp Temperature the item is being frozen at
	 * @param duration Duration to re-wind the clock by, in ticks
	 * @return The modified stack
	 */
	default ItemStack freeze(ItemStack stack, Level world, double temp, long duration){
		if(temp <= getFreezeTemperature()){
			setSpoilTime(stack, getAndInitSpoilTime(stack, world) + duration, 0);
		}
		return stack;
	}

	public static void addTooltip(ItemStack stack, @Nullable Level world, List<Component> tooltip){
		Item item = stack.getItem();
		if(item instanceof IPerishable sItem){
			long spoilTimestamp = getAndInitSpoilTime(stack, world);
			if(spoilTimestamp < 0){
				//Broken/new item; hasn't been configured properly
				tooltip.add(Component.translatable("tt.crossroads.boilerplate.spoilage.error"));
			}else if(world != null){
				if(isSpoiled(stack, world)){
					tooltip.add(Component.translatable("tt.crossroads.boilerplate.spoilage.spoiled"));
				}else{
					spoilTimestamp -= world.getGameTime();
					int days = (int) (spoilTimestamp / (20 * 60 * 60 * 24));
					spoilTimestamp -= days * 20L * 60L * 60L * 24L;
					int hours = (int) (spoilTimestamp / (20 * 60 * 60));
					spoilTimestamp -= hours * 20L * 60L * 60L;
					int minutes = (int) (spoilTimestamp / (20 * 60));
					spoilTimestamp -= minutes * 20L * 60L;
					int seconds = (int) (spoilTimestamp / 20);
					tooltip.add(Component.translatable("tt.crossroads.boilerplate.spoilage.remain", days, hours, minutes, seconds));
				}
			}
			tooltip.add(Component.translatable("tt.crossroads.boilerplate.spoilage.freezing", sItem.getFreezeTemperature()));//Freezing information
		}
	}
}

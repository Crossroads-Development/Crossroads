package com.Da_Technomancer.crossroads.API.witchcraft;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public interface ICultivatable extends IPerishable{

	public static final String FROZEN_KEY = "cr_was_frozen";

	default boolean wasFrozen(ItemStack stack){
		CompoundTag nbt = stack.getOrCreateTag();
		//Correct broken stacks, by setting a spoil time for a fresh item
		if(!nbt.contains(FROZEN_KEY)){
			return false;
		}
		return nbt.getBoolean(FROZEN_KEY);
	}

	default ItemStack setWasFrozen(ItemStack stack, boolean frozen){
		CompoundTag nbt = stack.getOrCreateTag();
		nbt.putBoolean(FROZEN_KEY, frozen);
		return stack;
	}

	@Override
	default ItemStack freeze(ItemStack stack, Level world, double temp, long duration){
		if(temp <= getFreezeTemperature()){
			//Damage the item if applicable
			stack = setWasFrozen(stack, true);
			setSpoilTime(stack, getSpoilTime(stack, world) + duration, 0);
		}

		return stack;
	}

	default ItemStack cultivate(ItemStack stack, Level world, long duration){
		return setSpoilTime(stack, getSpoilTime(stack, world) + duration, 0);
	}

	/**
	 * Note: Minimize calls, as most implementations create a new instance with each call
	 * @param self The item
	 * @param world The world
	 * @return The trade or recipe this item can perform, or null if none
	 */
	@Nullable
	CultivationTrade getCultivationTrade(ItemStack self, Level world);

	public static void addTooltip(ItemStack stack, @Nullable Level world, List<Component> tooltip){
		Item item = stack.getItem();
		if(item instanceof ICultivatable){
			ICultivatable sItem = (ICultivatable) item;
			long spoilTimestamp = sItem.getSpoilTime(stack, world);
			if(spoilTimestamp < 0){
				//Broken/new item; hasn't been configured properly
				tooltip.add(new TranslatableComponent("tt.crossroads.boilerplate.spoilage.error"));
			}else if(world != null){
				if(sItem.isSpoiled(stack, world)){
					tooltip.add(new TranslatableComponent("tt.crossroads.boilerplate.spoilage.spoiled"));
				}else{
					spoilTimestamp -= world.getGameTime();
					int days = (int) (spoilTimestamp / (20 * 60 * 60 * 24));
					spoilTimestamp -= days * 20 * 60 * 60 * 24;
					int hours = (int) (spoilTimestamp / (20 * 60 * 60));
					spoilTimestamp -= hours * 20 * 60 * 60;
					int minutes = (int) (spoilTimestamp / (20 * 60));
					spoilTimestamp -= minutes * 20 * 60;
					int seconds = (int) (spoilTimestamp / 20);
					tooltip.add(new TranslatableComponent("tt.crossroads.boilerplate.spoilage.remain", days, hours, minutes, seconds));
				}
			}
			if(sItem.wasFrozen(stack)){
				tooltip.add(new TranslatableComponent("tt.crossroads.boilerplate.spoilage.freezing.damaged", sItem.getFreezeTemperature()));//Freezing information
			}else{
				tooltip.add(new TranslatableComponent("tt.crossroads.boilerplate.spoilage.freezing", sItem.getFreezeTemperature()));//Freezing information
			}
		}
	}

	/**
	 * Whether the passed trade is plausibly the value which would be returned from getCultivationTrade
	 * This is used for validating cached values of getCultivationTrade.
	 * Do not recalculate the passed value unless necessary, for efficiency
	 * @param self The item
	 * @param trade The trade to validate
	 * @param world The world
	 * @return Whether the trade is plausible
	 */
	default boolean isTradeValid(ItemStack self, CultivationTrade trade, Level world){
		boolean spoiled = isSpoiled(self, world);
		return (trade == null) == spoiled;
	}

	static class CultivationTrade{

		public final ItemStack ingr1;
		public final ItemStack ingr2;
		public final ItemStack created;

		public CultivationTrade(ItemStack ingr1, ItemStack ingr2, ItemStack created){
			this.ingr1 = ingr1;
			this.ingr2 = ingr2;
			this.created = created;
		}

		@Override
		public boolean equals(Object o){
			if(this == o){
				return true;
			}
			if(o == null || getClass() != o.getClass()){
				return false;
			}
			CultivationTrade that = (CultivationTrade) o;
			return ingr1.equals(that.ingr1) && ingr2.equals(that.ingr2) && created.equals(that.created);
		}

		@Override
		public int hashCode(){
			return Objects.hash(ingr1, ingr2, created);
		}
	}
}

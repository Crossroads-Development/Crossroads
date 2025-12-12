package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.api.witchcraft.IPerishable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nullable;
import java.util.List;

public class ShavedIce extends Item implements IPerishable{

	private static final long LIFETIME = 20 * 60 * 5;
	private static final FoodProperties spoiledFoodProperties = new FoodProperties.Builder().nutrition(0).build();

	protected ShavedIce(){
		super(new Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.5F).build()));
		String name = "shaved_ice";
		CRItems.queueForRegister(name, this);
	}

	@Override
	public long getLifetime(){
		return LIFETIME;
	}

	@Override
	public double getFreezeTemperature(){
		return 0;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		IPerishable.addTooltip(stack, context.level(), tooltip);
		tooltip.add(Component.translatable("tt.crossroads.shaved_ice.desc"));
	}

	@Override
	@Nullable
	public FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity){
		if(IPerishable.isSpoiled(stack, entity == null ? null : entity.level())){
			return spoiledFoodProperties;
		}
		return super.getFoodProperties(stack, entity);
	}
}

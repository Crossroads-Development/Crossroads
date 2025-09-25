package com.Da_Technomancer.crossroads.items.witchcraft;

import com.Da_Technomancer.crossroads.api.witchcraft.EntityTemplate;
import com.Da_Technomancer.crossroads.api.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class BloodSample extends Item implements IPerishable{

	private static final long LIFETIME = 20 * 60 * 60 * 2;

	public BloodSample(){
		this("blood_sample");
	}

	public BloodSample(String name){
		super(new Item.Properties().stacksTo(1));//Not added to any creative tab
		CRItems.queueForRegister(name, this, null);
	}

	public ItemStack withEntityData(ItemStack stack, LivingEntity source){
		EntityTemplate template = EntityTemplate.getTemplateFromEntity(source);
		return withEntityData(stack, template);
	}

	public ItemStack withEntityData(ItemStack stack, EntityTemplate template){
		stack.set(CRItems.GENETICS_DATA, template);
		return stack;
	}

	public static EntityTemplate getEntityTypeData(ItemStack stack){
		return new EntityTemplate(stack.getOrDefault(CRItems.GENETICS_DATA, new EntityTemplate()));
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
		if(this != CRItems.separatedBloodSample){
			tooltip.add(Component.translatable("tt.crossroads.blood_sample.craft"));
		}
		EntityTemplate template = getEntityTypeData(stack);
		template.addTooltip(tooltip, 4);
		IPerishable.addTooltip(stack, context.level(), tooltip);
	}
}

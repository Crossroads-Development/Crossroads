package com.Da_Technomancer.crossroads.items.witchcraft;

import com.Da_Technomancer.crossroads.api.witchcraft.EntityTemplate;
import com.Da_Technomancer.crossroads.api.witchcraft.ICultivatable;
import com.Da_Technomancer.crossroads.api.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class Embryo extends Item implements ICultivatable{

	private static final long LIFETIME = 30 * 60 * 20;//30 minutes
	private static final int FREEZE_DEGRADE = 10;

	public Embryo(){
		super(new Item.Properties().stacksTo(1));//Not added to any creative tab
		String name = "embryo";
		CRItems.queueForRegister(name, this, null);
	}

	@Override
	public long getLifetime(){
		return LIFETIME;
	}

	@Override
	public double getFreezeTemperature(){
		return 0;
	}

	/**
	 * Writes the template and freeze history to the stack
	 * @param stack The stack to write to
	 * @param template The template, as would be returned from getEntityTypeData
	 * @param wasFrozen Whether this item should have been frozen in the past
	 */
	public void withEntityTypeData(ItemStack stack, EntityTemplate template, boolean wasFrozen){
		stack.set(CRItems.WAS_FROZEN_DATA, wasFrozen);
		stack.set(CRItems.GENETICS_DATA, template);
	}

	@Override
	public ItemStack doFreezeDamage(ItemStack stack, Level world){
		//damage the template
		EntityTemplate prevTemplate = getEntityTypeData(stack);
		stack.set(CRItems.GENETICS_DATA, prevTemplate.withQuality(prevTemplate.quality() - FREEZE_DEGRADE));
		return stack;
	}

	public EntityTemplate getEntityTypeData(ItemStack stack){
		return stack.getOrDefault(CRItems.GENETICS_DATA, EntityTemplate.DEFAULT);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		EntityTemplate template = getEntityTypeData(stack);
		template.addTooltip(tooltip, context.level());
		ICultivatable.addTooltip(stack, context.level(), tooltip);
	}

	@Nullable
	@Override
	public CultivationTrade getCultivationTrade(ItemStack self, Level world){
		//Produces (near)-copies of itself, using soul clusters &/or genetic plasmids as applicable

		if(IPerishable.isSpoiled(self, world)){
			return null;
		}
		ItemStack created = new ItemStack(this, 1);
		EntityTemplate template = getEntityTypeData(self);
		//The new item carries over the degradation due to freezing, but can be further damaged by freezing
		withEntityTypeData(created, template, false);

		ItemStack ingr1 = ItemStack.EMPTY;
		ItemStack ingr2 = ItemStack.EMPTY;
		int complexity = template.totalComplexity();
		if(complexity > 0){
			//Require as many mutagen as there is complexity
			ingr1 = new ItemStack(CRItems.mutagen, complexity);
		}
		int soulComplexity = template.totalSoulComplexity();
		if(soulComplexity > 0){
			if(ingr1.isEmpty()){
				ingr1 = new ItemStack(CRItems.soulCluster, soulComplexity);
			}else{
				ingr2 = new ItemStack(CRItems.soulCluster, soulComplexity);
			}
		}

		return new CultivationTrade(ingr1, ingr2, created);
	}
}

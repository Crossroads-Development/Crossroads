package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ArmorToolbelt extends TechnomancyArmor{

	public ArmorToolbelt(boolean reinforced){
		super(Type.LEGGINGS, reinforced);
		String name = reinforced ? "toolbelt_reinforced" : "toolbelt";
		CRItems.queueForRegister(name, this);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		super.appendHoverText(stack, context, tooltip, flag);
		tooltip.add(Component.translatable("tt.crossroads.toolbelt.desc"));
	}

	//All the magic happens in EventHandlerCommon
}

package com.Da_Technomancer.crossroads.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class CheatWandRotary extends HandCrank{

	protected CheatWandRotary(){
		super("cheat_wand_rotary", CRItems.CREATIVE_RARITY);
	}

	@Override
	protected int getRate(){
		return 10_000;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.crossroads.boilerplate.creative"));
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}
}

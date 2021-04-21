package com.Da_Technomancer.crossroads.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class CheatWandRotary extends HandCrank{

	protected CheatWandRotary(){
		super("cheat_wand_rotary");
	}

	@Override
	protected int getRate(){
		return 10_000;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.creative"));
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}
}

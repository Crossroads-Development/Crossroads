package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.api.rotary.RotaryUtil;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;

public class HandCrank extends Item{

	protected HandCrank(){
		this("hand_crank", Rarity.COMMON);
	}

	protected HandCrank(String name, Rarity rarity){
		super(new Properties().stacksTo(1).rarity(rarity));
		CRItems.queueForRegister(name, this);
	}

	protected int getRate(){
		return 100;
	}

	@Override
	public InteractionResult useOn(UseOnContext context){
		IAxleHandler axle;
		Direction side = context.getClickedFace().getOpposite();
		if((axle = context.getLevel().getCapability(CRCapabilities.AXLE_CAPABILITY, context.getClickedPos(), side)) != null){
			double signMult = -1;
			if(context.getPlayer() != null && context.getPlayer().isShiftKeyDown()){
				signMult *= -1;
			}
			signMult *= RotaryUtil.getCCWSign(side);
			axle.addEnergy(getRate() * signMult, true);
			context.getPlayer().getCooldowns().addCooldown(this, 4);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(Component.translatable("tt.crossroads.crank.desc", getRate()));
		tooltip.add(Component.translatable("tt.crossroads.crank.back", getRate()));
	}
}

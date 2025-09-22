package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;

public class CheatWandHeat extends Item{

	private static final int RATE = 100;

	protected CheatWandHeat(){
		super(new Properties().stacksTo(1).rarity(CRItems.CREATIVE_RARITY));
		String name = "cheat_wand_heat";
		CRItems.queueForRegister(name, this);
	}

	@Override
	public InteractionResult useOn(UseOnContext context){
		IHeatHandler cable = context.getLevel().getCapability(CRCapabilities.HEAT_CAPABILITY, context.getClickedPos(), null);
		if(cable != null){
			if(context.getPlayer() != null && context.getPlayer().isShiftKeyDown()){
				cable.addHeat(-RATE);
			}else{
				cable.addHeat(RATE);
			}
			context.getPlayer().getCooldowns().addCooldown(this, 4);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(Component.translatable("tt.crossroads.boilerplate.creative"));
		tooltip.add(Component.translatable("tt.crossroads.cheat_heat.desc", RATE));
		tooltip.add(Component.translatable("tt.crossroads.cheat_heat.cold", RATE));
	}
}

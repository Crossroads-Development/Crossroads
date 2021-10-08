package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.item.Item.Properties;

public class CheatWandHeat extends Item{

	private static final int RATE = 100;

	protected CheatWandHeat(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS).stacksTo(1));
		String name = "cheat_wand_heat";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public InteractionResult useOn(UseOnContext context){
		BlockEntity te = context.getLevel().getBlockEntity(context.getClickedPos());
		LazyOptional<IHeatHandler> heatOpt;
		if(te != null && (heatOpt = te.getCapability(Capabilities.HEAT_CAPABILITY, null)).isPresent()){
			IHeatHandler cable = heatOpt.orElseThrow(NullPointerException::new);
			if(context.getPlayer() != null && context.getPlayer().isShiftKeyDown()){
				cable.addHeat(-RATE);
			}else{
				cable.addHeat(RATE);
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("tt.crossroads.boilerplate.creative"));
		tooltip.add(new TranslatableComponent("tt.crossroads.cheat_heat.desc", RATE));
		tooltip.add(new TranslatableComponent("tt.crossroads.cheat_heat.cold", RATE));
	}
}

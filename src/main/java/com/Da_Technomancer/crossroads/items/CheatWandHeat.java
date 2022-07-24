package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.List;

public class CheatWandHeat extends Item{

	private static final int RATE = 100;

	protected CheatWandHeat(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS).stacksTo(1).rarity(CRItems.CREATIVE_RARITY));
		String name = "cheat_wand_heat";
		CRItems.toRegister.put(name, this);
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
			context.getPlayer().getCooldowns().addCooldown(this, 4);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.crossroads.boilerplate.creative"));
		tooltip.add(Component.translatable("tt.crossroads.cheat_heat.desc", RATE));
		tooltip.add(Component.translatable("tt.crossroads.cheat_heat.cold", RATE));
	}
}

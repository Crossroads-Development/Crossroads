package com.Da_Technomancer.crossroads.items.witchcraft;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class PotionExtension extends Item implements IPerishable{

	private static final long LIFETIME = 20 * 60 * 30;//30 minutes

	public PotionExtension(){
		super(new Item.Properties().stacksTo(1).tab(CRItems.TAB_CROSSROADS));
		String name = "potion_extension";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public long getLifetime(){
		return LIFETIME;
	}

	@Override
	public double getFreezeTemperature(){
		return -100;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(new TranslatableComponent("tt.crossroads.potion_extension.desc"));
		int penalty = CRConfig.injectionPermaPenalty.get();
		if(penalty > 0){
			tooltip.add(new TranslatableComponent("tt.crossroads.potion_extension.penalty", penalty));
		}
		IPerishable.addTooltip(stack, world, tooltip);
		tooltip.add(new TranslatableComponent("tt.crossroads.potion_extension.quip").setStyle(MiscUtil.TT_QUIP));
	}
}

package com.Da_Technomancer.crossroads.items.witchcraft;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

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
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag){
		tooltip.add(new TranslationTextComponent("tt.crossroads.potion_extension.desc"));
		int penalty = CRConfig.injectionPermaPenalty.get();
		if(penalty > 0){
			tooltip.add(new TranslationTextComponent("tt.crossroads.potion_extension.penalty", penalty));
		}
		IPerishable.addTooltip(stack, world, tooltip);
		tooltip.add(new TranslationTextComponent("tt.crossroads.potion_extension.quip").setStyle(MiscUtil.TT_QUIP));
	}
}

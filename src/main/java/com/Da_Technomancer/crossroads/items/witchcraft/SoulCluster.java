package com.Da_Technomancer.crossroads.items.witchcraft;

import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class SoulCluster extends Item{

	private final boolean large;

	public SoulCluster(boolean large){
		super(new Item.Properties().tab(CRItems.TAB_CROSSROADS));
		this.large = large;
		String name = large ? "soul_cluster" : "soul_shard";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public int getBurnTime(ItemStack itemStack){
		return large ? 1600 : 200;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag){
		tooltip.add(new TranslationTextComponent("tt.crossroads.soul_cluster"));
		if(!large){
			tooltip.add(new TranslationTextComponent("tt.crossroads.soul_cluster.small"));
		}
	}
}

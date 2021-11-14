package com.Da_Technomancer.crossroads.items.witchcraft;

import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

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
	public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType){
		return large ? 1600 : 400;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(new TranslatableComponent("tt.crossroads.soul_cluster"));
		if(!large){
			tooltip.add(new TranslatableComponent("tt.crossroads.soul_cluster.small"));
		}
	}
}

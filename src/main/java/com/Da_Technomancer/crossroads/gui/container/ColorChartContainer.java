package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class ColorChartContainer extends AbstractContainerMenu{

	@ObjectHolder("color_chart")
	private static MenuType<ColorChartContainer> type = null;

	public ColorChartContainer(int id, Inventory playerInv, @Nullable FriendlyByteBuf buf){
		super(type, id);
	}

	@Override
	public boolean stillValid(Player playerIn){
		return true;
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int fromSlot){
		return ItemStack.EMPTY;
	}
}

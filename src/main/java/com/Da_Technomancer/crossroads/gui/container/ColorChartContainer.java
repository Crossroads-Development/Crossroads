package com.Da_Technomancer.crossroads.gui.container;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class ColorChartContainer extends AbstractContainerMenu{

	protected static final MenuType<ColorChartContainer> TYPE = CRContainers.createConType(ColorChartContainer::new);

	public ColorChartContainer(int id, Inventory playerInv, @Nullable FriendlyByteBuf buf){
		super(TYPE, id);
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

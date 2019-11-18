package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class ColorChartContainer extends Container{

	@ObjectHolder("color_chart")
	private static ContainerType<ColorChartContainer> type = null;

	public ColorChartContainer(int id, PlayerInventory playerInv, @Nullable PacketBuffer buf){
		super(type, id);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn){
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int fromSlot){
		return ItemStack.EMPTY;
	}
}

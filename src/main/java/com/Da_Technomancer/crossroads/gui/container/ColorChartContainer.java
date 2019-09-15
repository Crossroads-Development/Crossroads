package com.Da_Technomancer.crossroads.gui.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ColorChartContainer extends Container{


	public ColorChartContainer(PlayerEntity player, World world, BlockPos pos){

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

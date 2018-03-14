package com.Da_Technomancer.crossroads.gui.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class AlchemyChartContainer extends Container{

	public AlchemyChartContainer(EntityPlayer player, World world){

	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn){
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int fromSlot){
		return ItemStack.EMPTY;
	}
}

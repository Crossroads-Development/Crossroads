package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.tileentities.rotary.StampMillTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class StampMillContainer extends Container{

	private StampMillTileEntity te;

	public StampMillContainer(IInventory playerInv, StampMillTileEntity te){
		this.te = te;

	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn){
		//TODO
		return false;
	}
}

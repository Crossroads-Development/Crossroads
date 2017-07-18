package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.tileentities.technomancy.PrototypePortTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class PrototypePortContainer extends Container{

	private PrototypePortTileEntity te;

	public PrototypePortContainer(PrototypePortTileEntity te){
		this.te = te;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn){
		return te.isUsableByPlayer(playerIn);
	}
}

package com.Da_Technomancer.crossroads.gui.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;

public class PrototypePortContainer extends Container{

	private PrototypePortTileEntity te;

	public PrototypePortContainer(PrototypePortTileEntity te){
		this.te = te;
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn){
		return te.isUsableByPlayer(playerIn);
	}
}

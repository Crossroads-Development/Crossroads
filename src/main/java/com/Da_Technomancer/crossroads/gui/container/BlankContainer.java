package com.Da_Technomancer.crossroads.gui.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;

public class BlankContainer extends Container{

	public BlankContainer(){
		
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn){
		return true;
	}
}

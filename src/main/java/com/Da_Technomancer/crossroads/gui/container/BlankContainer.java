package com.Da_Technomancer.crossroads.gui.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class BlankContainer extends Container{

	public BlankContainer(){
		
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn){
		return true;
	}
}

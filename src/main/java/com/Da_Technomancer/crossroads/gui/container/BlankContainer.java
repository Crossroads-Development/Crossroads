package com.Da_Technomancer.crossroads.gui.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;

/**
 * @deprecated The new vanilla container system discourages re-using an empty container class
 */
@Deprecated
public class BlankContainer extends Container{

	public BlankContainer(){
		
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn){
		return true;
	}
}

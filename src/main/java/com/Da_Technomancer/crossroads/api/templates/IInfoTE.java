package com.Da_Technomancer.crossroads.api.templates;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;

/**
 * Place on a TileEntity in order to cause measurement devices to show additional information. 
 */
public interface IInfoTE{

	/**
	 * @param chat Add info to this list, 1 line per entry.
	 * @param player The player using the info device.
	 * @param hit Portion of the block clicked/viewed
	 */
	void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit);
}

package com.Da_Technomancer.crossroads.API;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;

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
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit);

}

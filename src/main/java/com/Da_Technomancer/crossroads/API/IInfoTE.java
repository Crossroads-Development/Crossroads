package com.Da_Technomancer.crossroads.API;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

/**
 * Place on a TileEntity in order to cause measurement devices to show additional information. 
 */
public interface IInfoTE{

	/**
	 * @param chat Add info to this list, 1 line per entry. 
	 * @param device The device type calling this method. 
	 * @param player The player using the info device.
	 * @param side The viewed EnumFacing (only used by goggles).
	 */
	public void addInfo(ArrayList<String> chat, IInfoDevice device, EntityPlayer player, @Nullable EnumFacing side);
	
}

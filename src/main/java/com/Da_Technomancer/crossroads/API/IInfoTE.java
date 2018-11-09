package com.Da_Technomancer.crossroads.API;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * Place on a TileEntity in order to cause measurement devices to show additional information. 
 */
public interface IInfoTE{

	/**
	 * @param chat Add info to this list, 1 line per entry.
	 * @param player The player using the info device.
	 * @param side The viewed EnumFacing (only used by goggles).
	 * @param hitX The hitX
	 * @param hitY The hitY
	 * @param hitZ The hitZ
	 */
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ);

}

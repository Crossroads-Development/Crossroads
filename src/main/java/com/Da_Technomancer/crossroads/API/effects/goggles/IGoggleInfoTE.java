package com.Da_Technomancer.crossroads.API.effects.goggles;

import java.util.ArrayList;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.enums.GoggleLenses;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

/**
 * Place on a TileEntity in order to cause goggles to show additional information. 
 */
public interface IGoggleInfoTE{

	/**
	 * @param chat Add info to this list, 1 line per entry. 
	 * @param lens The lens type calling this method. Not all types will call this method at all.
	 * @param player The player wearing the goggles.
	 * @param side The viewed EnumFacing.
	 */
	public void addInfo(ArrayList<String> chat, GoggleLenses lens, EntityPlayer player, @Nullable EnumFacing side);
}

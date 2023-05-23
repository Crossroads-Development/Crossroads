package com.Da_Technomancer.crossroads.api.technomancy;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface IGoggleEffect{

	public static final IGoggleEffect EMPTY = (world, player) -> {};

	/**
	 * Called every tick on the server side while goggles with the correct lens are worn.
	 */
	public void armorTick(Level world, Player player);
	
}

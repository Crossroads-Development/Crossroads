package com.Da_Technomancer.crossroads.API.technomancy;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public interface IGoggleEffect{
	
	/**Called every tick on the server side while goggles with the correct lens are worn.
	 * Instead of printing chat directly (except in special cases like element discovery),
	 * each line of chat should be added to the List separately. 
	 */
	public void armorTick(World world, EntityPlayer player, ArrayList<String> chat, @Nullable RayTraceResult ray);
	
}

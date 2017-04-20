package com.Da_Technomancer.crossroads.API.technomancy;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class VoidGoggleEffect implements IGoggleEffect{

	@Override
	public void armorTick(World world, EntityPlayer player, ArrayList<String> chat, RayTraceResult ray){
		//Empty effect, the actual effect is done through EventHandlers that check for the void lens.
	}
}
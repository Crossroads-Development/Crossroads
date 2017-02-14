package com.Da_Technomancer.crossroads.API.technomancy;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class VoidGoggleEffect implements IGoggleEffect{

	@Override
	public void armorTick(World world, EntityPlayer player, ArrayList<String> chat){
		return;
	}
}
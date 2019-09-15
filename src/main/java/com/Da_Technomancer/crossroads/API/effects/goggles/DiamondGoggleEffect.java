package com.Da_Technomancer.crossroads.API.effects.goggles;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class DiamondGoggleEffect implements IGoggleEffect{

	@Override
	public void armorTick(World world, PlayerEntity player, ArrayList<String> chat, RayTraceResult ray){
		//Effect in SendGoggleConfigureToServer
	}
}
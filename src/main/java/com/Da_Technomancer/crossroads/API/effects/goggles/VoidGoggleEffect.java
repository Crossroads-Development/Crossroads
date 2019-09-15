package com.Da_Technomancer.crossroads.API.effects.goggles;

import java.util.ArrayList;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class VoidGoggleEffect implements IGoggleEffect{

	@Override
	public void armorTick(World world, PlayerEntity player, ArrayList<String> chat, RayTraceResult ray){
		//Empty effect, the actual effect is done through EventHandlers that check for the void lens.
	}
}
package com.Da_Technomancer.crossroads.API.effects.goggles;

import com.Da_Technomancer.crossroads.items.OmniMeter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class QuartzGoggleEffect implements IGoggleEffect{

	@Override
	public void armorTick(World world, PlayerEntity player, ArrayList<String> chat, RayTraceResult ray){
		if(ray == null){
			return;
		}

		OmniMeter.measure(chat, player, player.world, ray.getBlockPos(), ray.sideHit, (float) ray.hitVec.x - ray.getBlockPos().getX(), (float) ray.hitVec.y - ray.getBlockPos().getY(), (float) ray.hitVec.z - ray.getBlockPos().getZ());
	}
}
package com.Da_Technomancer.crossroads.API.effects.goggles;

import com.Da_Technomancer.crossroads.items.OmniMeter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;

public class QuartzGoggleEffect implements IGoggleEffect{

	@Override
	public void armorTick(World world, PlayerEntity player, ArrayList<ITextComponent> chat, BlockRayTraceResult ray){
		if(ray == null){
			return;
		}

		OmniMeter.measure(chat, player, player.world, ray.getPos(), ray.getFace(), ray);
	}
}
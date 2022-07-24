package com.Da_Technomancer.crossroads.effects.goggles_effects;

import com.Da_Technomancer.crossroads.api.technomancy.IGoggleEffect;
import com.Da_Technomancer.crossroads.items.OmniMeter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;

public class QuartzGoggleEffect implements IGoggleEffect{

	@Override
	public void armorTick(Level world, Player player, ArrayList<Component> chat, BlockHitResult ray){
		if(ray == null){
			return;
		}

		OmniMeter.measure(chat, player, player.level, ray.getBlockPos(), ray.getDirection(), ray);
	}
}
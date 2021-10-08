package com.Da_Technomancer.crossroads.API.effects.goggles;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public class VoidGoggleEffect implements IGoggleEffect{

	@Override
	public void armorTick(Level world, Player player, ArrayList<Component> chat, BlockHitResult ray){
		//Empty effect, the actual effect is done through EventHandlers that check for the void lens.
	}
}
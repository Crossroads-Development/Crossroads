package com.Da_Technomancer.crossroads.effects.goggles_effects;

import com.Da_Technomancer.crossroads.api.technomancy.IGoggleEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;

public class EmeraldGoggleEffect implements IGoggleEffect{

	@Override
	public void armorTick(Level world, Player player, ArrayList<Component> chat, BlockHitResult ray){
		player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, false, false));
	}
}
package com.Da_Technomancer.crossroads.API.effects.goggles;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

public interface IGoggleEffect{
	
	/**
	 * Called every tick on the server side while goggles with the correct lens are worn.
	 * Instead of printing chat directly (except in special cases like element discovery),
	 * each line of chat should be added to the List separately. 
	 */
	public void armorTick(World world, PlayerEntity player, ArrayList<ITextComponent> chat, @Nullable BlockRayTraceResult ray);
	
}

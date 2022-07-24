package com.Da_Technomancer.crossroads.effects.alchemy_effects;

import com.Da_Technomancer.crossroads.api.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.api.alchemy.IAlchEffect;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SpawnItemAlchemyEffect implements IAlchEffect{

	private final Item spawnedItem;

	public SpawnItemAlchemyEffect(Item spawnedItem){
		this.spawnedItem = spawnedItem;
	}

	@Override
	public void doEffect(Level world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap reags){
		if(phase == EnumMatterPhase.SOLID){//Very important requirement- otherwise we spawn a massive number of these, creating a dupe bug
			Containers.dropItemStack(world, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), new ItemStack(spawnedItem, amount));
		}
	}

	@Override
	public Component getName(){
		return Component.translatable("effect.spawn_item", spawnedItem.getDescription().getString());
	}
}

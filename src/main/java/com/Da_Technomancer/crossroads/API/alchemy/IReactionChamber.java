package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IReactionChamber extends IAlchemyContainer{
	
	@Nullable
	public IReagent getCatalyst();
	
	public default boolean isCharged(){
		return false;
	}
	
	public void addItem(ItemStack stack);
	
	public World getWorld();
	
	public BlockPos getPos();
}

package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IReactionChamber{
	
	public boolean isGlass();
	
	@Nullable
	public IReagent getCatalyst();
	
	/**
	 * Note: might be called several times in quick succession. 
	 */
	public void destroyChamber();
	
	/**
	 * Array MUST be of size {@link AlchemyCraftingManager#RESERVED_REAGENT_COUNT} + {@link AlchemyCraftingManager#DYNAMIC_REAGENT_COUNT}. May contain null elements. 
	 * @return An array of the contained reagents. 
	 */
	@Nonnull
	public IReagent[] getReagants();
	
	public default boolean isCharged(){
		return false;
	}
	
	public World getWorld();
	
	public BlockPos getPos();
}

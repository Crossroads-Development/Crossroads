package com.Da_Technomancer.crossroads.API.alchemy;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IReactionChamber{
	
	public boolean isGlass();
	
	@Nullable
	public IReagent getCatalyst();
	
	@Nullable
	public IReagent getSolid();
	
	public void clearSolid();
	
	/**
	 * Note: might be called several times in quick succession. 
	 */
	public void destroyChamber();
	
	@Nonnull
	public List<IReagent> getNonSolidReagants();
	
	/**
	 * Adds a solidified material to the chamber. 
	 */
	public void addSolid(IReagent solid);
	
	public default boolean isCharged(){
		return false;
	}
	
	public World getWorld();
	
	public BlockPos getPos();
}

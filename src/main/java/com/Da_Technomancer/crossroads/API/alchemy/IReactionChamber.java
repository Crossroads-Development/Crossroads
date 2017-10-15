package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nullable;

import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IReactionChamber extends IAlchemyContainer{
	
	@Nullable
	public Reagent getCatalyst();
	
	public default boolean isCharged(){
		return false;
	}
	
	/**
	 * Note: might be called several times in quick succession. 
	 */
	public void destroyChamber();
	
	
	/**
	 * Adds temporary particles for visual effect after a reaction. Note that x, y, and z Offset are poorly named, and together with the particleSpeed, can control the color for several particle types. I don't understand why either. 
	 * @param particleType
	 * @param numberOfParticles
	 * @param xOffset
	 * @param yOffset
	 * @param zOffset
	 * @param particleSpeed
	 */
	public void addVisualEffect(EnumParticleTypes particleType, int numberOfParticles, double xOffset, double yOffset, double zOffset, double particleSpeed);
	
	public World getWorld();
	
	public BlockPos getPos();
}

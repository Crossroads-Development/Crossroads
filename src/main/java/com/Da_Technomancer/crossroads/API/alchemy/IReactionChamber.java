package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * To be placed on things that store alchemy reagents and allow reactions.  *
 */
public interface IReactionChamber{
	
	public boolean isGlass();

	/**
	 * Array MUST be of size {@link AlchemyCore#REAGENT_COUNT}. May contain null elements. 
	 * @return An array of the contained reagents. 
	 */
	@Nonnull
	public Reagent[] getReagants();
	
	public double getIntegrityCapacity();
	
	/**
	 * Not the same as temperature.
	 * @return
	 */
	public double getHeat();
	
	public void setHeat(double heatIn);
	
	public default void addHeat(double heatChange){
		setHeat(getHeat() + heatChange);
	}
	
	/**
	 * In degrees centigrade. 
	 * @return
	 */
	public default double getTemp(){
		double cont = getContent();
		return cont == 0 ? 0 : (getHeat() / cont) + 273D;
	}
	
	public default double getContent(){
		double amount = 0;
		for(Reagent reag : getReagants()){
			if(reag != null){
				amount += reag.getAmount();
			}
		}
		return amount;
	}
	
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

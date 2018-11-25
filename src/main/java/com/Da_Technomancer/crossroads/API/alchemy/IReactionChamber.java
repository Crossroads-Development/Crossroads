package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;

/**
 * To be placed on things that store alchemy reagents and allow reactions.  *
 */
public interface IReactionChamber{

	@Nonnull
	public ReagentMap getReagants();
	
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
		return cont == 0 ? 0 : (getHeat() / cont) - 273D;
	}
	
	public default int getContent(){
		int amount = 0;
		ReagentMap map = getReagants();
		for(Integer qty : map.values()){
			if(qty != null){
				amount += qty;
			}
		}
		return amount;
	}
	
	public default boolean isCharged(){
		return false;
	}
	
	public int getReactionCapacity();
	
	/**
	 * Note: might be called several times in quick succession. 
	 */
	public void destroyChamber();
	
	
	/**
	 * Adds temporary particles for visual effect after a reaction. 
	 * @param particleType
	 * @param speedX
	 * @param speedY
	 * @param speedZ
	 * @param particleArgs
	 */
	public void addVisualEffect(EnumParticleTypes particleType, double speedX, double speedY, double speedZ, int... particleArgs);
	
	/**
	 * Drops the provided ItemStack into the world
	 * @param stack
	 */
	public void dropItem(ItemStack stack);
}

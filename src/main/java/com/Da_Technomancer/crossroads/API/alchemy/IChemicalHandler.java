package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.EnumFacing;

/**
 * Allows the transfer of alchemical reagents and heat. 
 * @author Max
 *
 */
public interface IChemicalHandler{
	
	public double getContent();
	
	public double getTransferCapacity();
	
	public default double getTemp(){
		double cont = getContent();
		return cont == 0 ? 0 : (getHeat() / cont) - 273D;
	}
	
	public double getHeat();
	
	public void setHeat(double heat);
	
	public default void addHeat(double change){
		setHeat(getHeat() + change);
	}
	
	/**
	 * @param reag A standard reagent storage array. Moved reagents will be taken from it directly, so it should be mutable and write back to the caller.
	 * @param side The side this is calling (for programming convenience). 
	 * @param caller An IChemicalHandler calling this for transferring heat. If null, this acts as if the transferred reagent is 20*C and no heat will be removed from the source. 
	 * @return Whether anything in reag was changed. 
	 */
	public boolean insertReagents(Reagent[] reag, EnumFacing side, @Nullable IChemicalHandler caller);
	
	@Nonnull
	public EnumTransferMode getMode(EnumFacing side);
	
	@Nonnull
	public EnumContainerType getChannel(EnumFacing side);

}

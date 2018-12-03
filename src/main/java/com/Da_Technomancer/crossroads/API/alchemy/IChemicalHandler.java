package com.Da_Technomancer.crossroads.API.alchemy;

import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

/**
 * Allows the transfer of alchemical reagents and heat. 
 */
public interface IChemicalHandler{
	
//	public int getContent();
	
	/**
	 * @param type The reagent to check
	 * @return The contained amount of the passed reagent type.
	 */
	public int getContent(IReagent type);
	
	public int getTransferCapacity();

	/**
	 * @return The temperature in degrees C
	 */
	public double getTemp();
	
//	public double getHeat();
//
//	public void setHeat(double heat);
//
//	public default void addHeat(double change){
//		setHeat(getHeat() + change);
//	}
	
	/**
	 * @param reag A standard reagent storage map. Moved reagents will be taken from it directly, so it should be mutable and write back to the caller.
	 * @param side The side this is calling (for programming convenience). 
	 * @param caller An IChemicalHandler calling this for transferring heat.
	 * @return Whether anything in reag was changed. 
	 */
	public default boolean insertReagents(ReagentMap reag, EnumFacing side, @Nonnull IChemicalHandler caller){
		return insertReagents(reag, side, caller, false);
	}
	
	/**
	 * @param reag A standard reagent storage map. Moved reagents will be taken from it directly, so it should be mutable and write back to the caller.
	 * @param side The side this is calling (for programming convenience). 
	 * @param caller An IChemicalHandler calling this for transferring heat.
	 * @param ignorePhase If true, ignore phase movement rules. 
	 * @return Whether anything in reag was changed. 
	 */
	public boolean insertReagents(ReagentMap reag, EnumFacing side, @Nonnull IChemicalHandler caller, boolean ignorePhase);
	
	@Nonnull
	public EnumTransferMode getMode(EnumFacing side);
	
	@Nonnull
	public EnumContainerType getChannel(EnumFacing side);

}

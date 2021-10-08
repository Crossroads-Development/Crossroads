package com.Da_Technomancer.crossroads.API.alchemy;

import net.minecraft.core.Direction;

import javax.annotation.Nonnull;

/**
 * Allows the transfer of alchemical reagents and heat. 
 */
public interface IChemicalHandler{
	
	/**
	 * @param type The reagent to check
	 * @return The contained amount of the passed reagent type.
	 */
	int getContent(IReagent type);
	
	int getTransferCapacity();

	/**
	 * @return The temperature in degrees C
	 */
	double getTemp();
	
	/**
	 * @param reag A standard reagent storage map. Moved reagents will be taken from it directly, so it should be mutable and write back to the caller.
	 * @param side The side this is calling (for programming convenience- allows returning the same handler to multiple sides).
	 * @param caller An IChemicalHandler calling this for transferring heat.
	 * @return Whether anything in reag was changed. 
	 */
	default boolean insertReagents(ReagentMap reag, Direction side, @Nonnull IChemicalHandler caller){
		return insertReagents(reag, side, caller, false);
	}
	
	/**
	 * @param reag A standard reagent storage map. Moved reagents will be taken from it directly, so it should be mutable and write back to the caller.
	 * @param side The side this is calling (for programming convenience- allows returning the same handler to multiple sides).
	 * @param caller An IChemicalHandler calling this for transferring heat.
	 * @param ignorePhase If true, ignore phase movement rules. 
	 * @return Whether anything in reag was changed. 
	 */
	boolean insertReagents(ReagentMap reag, Direction side, @Nonnull IChemicalHandler caller, boolean ignorePhase);
	
	@Nonnull
	EnumTransferMode getMode(Direction side);

	/**
	 * GLASS and CRYSTAL will refuse to directly connect, but NONE will connect to everything
	 * Do not return the NONE channel unless this block either a) cannot output reagents or b) will prevent "destructive" materials from going into something on the glass channel
	 * @param side The calling side
	 * @return What channel this is, for connections
	 */
	@Nonnull
	EnumContainerType getChannel(Direction side);

}

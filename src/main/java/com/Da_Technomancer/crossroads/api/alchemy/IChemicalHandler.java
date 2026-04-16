package com.Da_Technomancer.crossroads.api.alchemy;

import net.minecraft.core.Direction;

import javax.annotation.Nonnull;
import java.util.function.Function;

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
	 * Attempts to insert all reagents in reag into this handler. Handler will accept as many as it chooses to, leave the rest in reag
	 * @param reag A standard reagent storage map. Moved reagents will be taken from it directly, so it should be mutable and write back to the caller.
	 * @param side The side this is calling (for programming convenience- allows returning the same handler to multiple sides).
	 * @param caller An IChemicalHandler calling this for transferring heat.
	 * @return Whether anything in reag was changed.
	 * @deprecated Switch to another version
	 */
	@Deprecated
	default boolean insertReagents(ReagentMap reag, Direction side, @Nonnull IChemicalHandler caller){
		return insertReagents(reag, side, caller, false);
	}

	/**
	 * Attempts to insert all reagents in reag into this handler. Handler will accept as many as it chooses to, leave the rest in reag
	 * @param reag A standard reagent storage map. Moved reagents will be taken from it directly, so it should be mutable and write back to the caller.
	 * @param side The side this is calling (for programming convenience- allows returning the same handler to multiple sides).
	 * @param caller An IChemicalHandler calling this for transferring heat.
	 * @param ignorePhase If true, ignore phase movement rules.
	 * @return Whether anything in reag was changed.
	 */
	default boolean insertReagents(ReagentMap reag, Direction side, @Nonnull IChemicalHandler caller, boolean ignorePhase){
		return insertReagents(reag, side, caller, ignorePhase ? (reagent) -> Integer.MAX_VALUE : new Function<IReagent, Integer>(){
			private final double sourceTemp = reag.getTempC();

			@Override
			public Integer apply(IReagent reagent){
				return reagent.getPhase(sourceTemp).canFlow(side.getOpposite()) ? Integer.MAX_VALUE : 0;
			}
		});
	}

	/**
	 * Attempts to insert all reagents in reag into this handler. Handler will accept as many as it chooses to, leave the rest in reag
	 * @param reag A standard reagent storage map. Moved reagents will be taken from it directly, so it should be mutable and write back to the caller.
	 * @param side The side this is calling (for programming convenience- allows returning the same handler to multiple sides).
	 * @param caller An IChemicalHandler calling this for transferring heat.
	 * @param maximumTransferQuantities A function setting a maximum amount of each type of reagent that is allowed to be transferred from reag. The destination is allowed to set its own limits. Can be used to enforce phase movement rules. Use Integer.MAX_VALUE for no limit.
	 * @return Whether anything in reag was changed.
	 */
	boolean insertReagents(ReagentMap reag, Direction side, @Nonnull IChemicalHandler caller, Function<IReagent, Integer> maximumTransferQuantities);

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

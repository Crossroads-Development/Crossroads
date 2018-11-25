package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import net.minecraft.util.EnumFacing;

/**
 * Allows the transfer of alchemical reagents and heat. 
 */
public interface IChemicalHandler{
	
	public int getContent();
	
	/**
	 * @param type The id of a reagent type
	 * @return The contained amount of the passed reagent type id.
	 */
	public int getContent(String type);
	
	public int getTransferCapacity();
	
	public default double getTemp(){
		double cont = getContent();
		return cont == 0 ? 0 : HeatUtil.toCelcius(getHeat() / cont);
	}
	
	public double getHeat();
	
	public void setHeat(double heat);
	
	public default void addHeat(double change){
		setHeat(getHeat() + change);
	}
	
	/**
	 * @param reag A standard reagent storage map. Moved reagents will be taken from it directly, so it should be mutable and write back to the caller.
	 * @param side The side this is calling (for programming convenience). 
	 * @param caller An IChemicalHandler calling this for transferring heat. If null, this acts as if the transferred reagent is 20*C (or ambient temperature if possible) and no heat will be removed from the source.
	 * @return Whether anything in reag was changed. 
	 */
	public default boolean insertReagents(ReagentMap reag, EnumFacing side, @Nullable IChemicalHandler caller){
		return insertReagents(reag, side, caller, false);
	}
	
	/**
	 * @param reag A standard reagent storage map. Moved reagents will be taken from it directly, so it should be mutable and write back to the caller.
	 * @param side The side this is calling (for programming convenience). 
	 * @param caller An IChemicalHandler calling this for transferring heat. If null, this acts as if the transferred reagent is 20*C and no heat will be removed from the source. 
	 * @param ignorePhase If true, ignore phase movement rules. 
	 * @return Whether anything in reag was changed. 
	 */
	public boolean insertReagents(ReagentMap reag, EnumFacing side, @Nullable IChemicalHandler caller, boolean ignorePhase);
	
	@Nonnull
	public EnumTransferMode getMode(EnumFacing side);
	
	@Nonnull
	public EnumContainerType getChannel(EnumFacing side);

}

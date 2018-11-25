package com.Da_Technomancer.crossroads.API.alchemy;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;

public interface IReagent{

	/**
	 * @return A human readable name
	 * Note: May return different values based on language or on client vs server side
	 */
	public default String getName(){
		return new TextComponentTranslation("reagent." + getId()).getUnformattedComponentText();
	}
	
	/**
	 * @return The melting temperature. Must be less than boiling temperature. Setting below absolute-zero will disable freezing. 
	 */
	public double getMeltingPoint();
	
	/**
	 * @return The boiling temperature. Must be greater than melting temperature. Setting below absolute-zero will disable condensing. 
	 */
	public double getBoilingPoint();
	
	public default boolean canGlassContain(){
		return true;
	}
	
	public default boolean destroysBadContainer(){
		return false;
	}
	
	public String getId();
	
	/**
	 * Gets the (purely visual) color. 
	 * @param phase
	 * @return A color for rendering. Alpha is used. 
	 */
	public Color getColor(EnumMatterPhase phase);

	/**
	 * @param world
	 * @param pos
	 * @param amount
	 * @param heat
	 * @param phase This is NOT necessarily the actual phase, this is the phase the reagent should pretend to be/act as when performing the effect. EnumMatterPhase.FLAME when performed as part of a phelostigen effect.
	 * @param contents
	 */
	public default void onRelease(World world, BlockPos pos, int amount, double heat, EnumMatterPhase phase, ReagentMap contents){
		
	}
	
	public default ReagentStack getReagentFromStack(ItemStack stack){
		return ReagentStack.EMPTY;
	}
	
	/**
	 * @param reag The reagent
	 * @return The matching solid ItemStack. ItemStack.EMPTY if there either isn't enough material (or cannot be solidifed for any other reason). 
	 */
	public default ItemStack getStackFromReagent(ReagentStack reag){
		return ItemStack.EMPTY;
	}
	
	public default boolean isLockedFlame(){
		return false;
	}

	public List<ItemStack> getJEISolids();

	/**
	 * @param temp Current temperature. Optional, only used if phase hasn't been set yet to update the phase. If phase should already have been set, this can be left as 0.
	 * @return The phase
	 */
	@Nonnull
	public default EnumMatterPhase getPhase(double temp){
		if(isLockedFlame()){
			return EnumMatterPhase.FLAME;
		}else if(temp >= getBoilingPoint()){
			return EnumMatterPhase.GAS;
		}else if(temp < getMeltingPoint()){
			return EnumMatterPhase.SOLID;
		}else{
			return EnumMatterPhase.LIQUID;
		}
	}
}

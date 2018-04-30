package com.Da_Technomancer.crossroads.API.alchemy;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.awt.*;

public interface IReagent{

	/**
	 * @return A human readable name. 
	 */
	public String getName();
	
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
	
	public int getIndex();
	
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
	public default void onRelease(World world, BlockPos pos, double amount, double heat, EnumMatterPhase phase, ReagentStack[] contents){
		
	}
	
	@Nullable
	public default ReagentStack getReagentFromStack(ItemStack stack){
		return null;
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
}

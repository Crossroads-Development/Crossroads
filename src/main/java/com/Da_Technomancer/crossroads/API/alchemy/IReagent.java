package com.Da_Technomancer.crossroads.API.alchemy;

import java.awt.Color;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
	
	/**
	 * @return The type of solvent this acts as. 
	 */
	@Nullable
	public default EnumSolventType solventType(){
		return null;
	}
	
	/**
	 * @return The type of solvent this needs to be in to dissolve. 
	 */
	@Nullable
	public default EnumSolventType soluteType(){
		return solventType();
	}
	
	public default boolean canGlassContain(){
		return true;
	}
	
	public default boolean destroysBadContainer(){
		return false;
	}
	
	public int getIndex();
	
	/**
	 * Gets the (purely visual) color. 
	 * @param phase Will never be called for solid phase. 
	 * @return A color for rendering. Alpha is used. 
	 */
	public Color getColor(EnumMatterPhase phase);
	
	public default void onRelease(World world, BlockPos pos, double amount, EnumMatterPhase phase, ReagentStack[] contents){
		
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

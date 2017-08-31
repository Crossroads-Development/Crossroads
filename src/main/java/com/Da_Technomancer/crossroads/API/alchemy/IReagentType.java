package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IReagentType{

	public String getName();
	
	public double getMeltingPoint();
	
	public double getBoilingPoint();
	
	/**
	 * @return The type of solvent this acts as. 
	 */
	@Nullable
	public default SolventType solventType(){
		return null;
	}
	
	/**
	 * @return The type of solvent this needs to be in to dissolve. 
	 */
	@Nullable
	public default SolventType soluteType(){
		return solventType();
	}
	
	public default boolean canGlassContain(double temp){
		return true;
	}
	
	public default boolean destroysBadContainer(double temp){
		return false;
	}
	
	public default void onRelease(World world, BlockPos pos, double amount, MatterPhase phase){
		
	}
	
	@Nullable
	public default IReagent getReagentFromStack(ItemStack stack){
		return null;
	}
	
	/**
	 * @param reag The reagent
	 * @return The matching solid ItemStack. ItemStack.EMPTY if there either isn't enough material (or cannot be solidifed for any other reason). 
	 */
	public default ItemStack getStackFromReagent(IReagent reag){
		return ItemStack.EMPTY;
	}
	
	public default boolean isAlkhest(){
		return false;
	}
	
	public default boolean isAntiAlkhest(){
		return false;
	}
}

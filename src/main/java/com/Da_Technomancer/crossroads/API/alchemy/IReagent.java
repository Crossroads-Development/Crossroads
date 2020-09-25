package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.effects.alchemy.IAlchEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.awt.*;

public interface IReagent{

	/**
	 * Gets the name of this reagent.
	 * Note: May return different values based on language or on client vs server side
	 * Do not use this name for logic- use getId() instead
	 * @return A human readable name
	 */
	default String getName(){
		return MiscUtil.localize("reagent." + getID());
	}
	
	/**
	 * @return The melting temperature in C. Must be less than boiling temperature. Setting below absolute-zero will disable freezing.
	 */
	double getMeltingPoint();
	
	/**
	 * @return The boiling temperature in C. Must be greater than or equal to melting temperature. Setting below absolute-zero will disable condensing.
	 */
	double getBoilingPoint();
	
	default boolean requiresCrystal(){
		return false;
	}
	
	default boolean destroysBadContainer(){
		return false;
	}
	
	String getID();

	/**
	 * Do not modify the returned value- it is unfortunately mutable
	 * @return A fluidstack representing the fluid equivalent of 1 unit of this reagent. EMPTY means no equivalent.
	 */
	default FluidStack getFluid(){
		return FluidStack.EMPTY;
	}

	default int getFlameRadius(int amount){
		return 0;
	}

	/**
	 * Gets the (purely visual) color. 
	 * @param phase The current phase
	 * @return A color for rendering. Alpha is used. 
	 */
	Color getColor(EnumMatterPhase phase);

	@Nonnull
	IAlchEffect getEffect();
	
	/**
	 * @param reag The reagent
	 * @return The matching solid ItemStack. ItemStack.EMPTY if there either isn't enough material (or cannot be solidifed for any other reason). 
	 */
	default ItemStack getStackFromReagent(ReagentStack reag){
		return ItemStack.EMPTY;
	}
	
	default boolean isLockedFlame(){
		return false;
	}

	/**
	 * Used for JEI support. Do not use this for game logic.
	 * @return A tag containing all items that can be converted into the solid form of this reagent
	 */
	ITag<Item> getJEISolids();

	/**
	 * @param temp Current temperature in C. Optional, only used if phase hasn't been set yet to update the phase. If phase should already have been set, this can be left as 0.
	 * @return The phase
	 */
	@Nonnull
	default EnumMatterPhase getPhase(double temp){
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

package com.Da_Technomancer.crossroads.api.alchemy;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.crafting.FluidIngredient;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.awt.*;

public interface IReagent{

	/**
	 * Gets the name of this reagent.
	 * Note: May return different values based on language or on client vs server side
	 * Do not use this name for logic- use getId() instead
	 * @return A human readable name
	 * @deprecated On client side, use MiscUtil.localize(getLocalizationKey()); on server side, make the localization occur on the client
	 */
	@Deprecated
	default String getName(){
		return MiscUtil.localize(getLocalizationKey());
	}

	/**
	 * Gets the translation lookup key for the reagent name
	 * Do not use this name for logic- use getId() instead
	 * @return Localization key for the reagent name
	 */
	default String getLocalizationKey(){
		return "reagent." + getID();
	}

	/**
	 * @return The melting temperature in C. Must be less than boiling temperature. Setting below absolute-zero will disable freezing.
	 */
	double getMeltingPoint();

	/**
	 * @return The boiling temperature in C. Must be greater than or equal to melting temperature. Setting below absolute-zero will disable condensing.
	 */
	double getBoilingPoint();

	boolean requiresCrystal();

	boolean destroysBadContainer();

	String getID();

	/**
	 * @return A FluidIngredient representing the fluid equivalent of 1 unit of this reagent. EMPTY means no equivalent.
	 */
	FluidIngredient getFluid();

	/**
	 *
	 * @return The quantity of fluid associated with 1 unit of this reagent. May be 0 only if getFluid() returns FluidIngredient.EMPTY
	 */
	int getFluidQty();

	int getFlameRadius(int amount);

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
	ItemStack getStackFromReagent(ReagentStack reag);

	boolean isLockedFlame();

	/**
	 * Used for JEI support. Do not use this for game logic.
	 * @return A tag containing all items that can be converted into the solid form of this reagent
	 */
	TagKey<Item> getJEISolids();

	/**
	 * @param temp Current temperature in C
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

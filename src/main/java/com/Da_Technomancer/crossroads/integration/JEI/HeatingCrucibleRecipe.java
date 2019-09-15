package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.items.crafting.RecipePredicate;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class HeatingCrucibleRecipe{

	protected final RecipePredicate<ItemStack> in;
	protected final FluidStack out;

	public HeatingCrucibleRecipe(RecipePredicate<ItemStack> in, FluidStack out){
		this.in = in;
		this.out = out;
	}
}

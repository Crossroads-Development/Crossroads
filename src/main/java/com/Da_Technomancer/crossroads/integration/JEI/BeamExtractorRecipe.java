package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import net.minecraft.item.ItemStack;

public class BeamExtractorRecipe{

	protected final ItemStack in;
	protected final BeamUnit out;

	public BeamExtractorRecipe(ItemStack in, BeamUnit out){
		this.in = in;
		this.out = out;
	}
}

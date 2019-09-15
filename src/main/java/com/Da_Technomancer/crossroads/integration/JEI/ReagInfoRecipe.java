package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ReagInfoRecipe{

	protected final ReagIngr type;
	protected final List<ItemStack> solid;

	public ReagInfoRecipe(IReagent type){
		this.type = new ReagIngr(type, 0);
		solid = type.getJEISolids();
	}
}

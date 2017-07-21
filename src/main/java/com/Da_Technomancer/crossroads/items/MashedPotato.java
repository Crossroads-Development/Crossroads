package com.Da_Technomancer.crossroads.items;

import net.minecraft.item.ItemFood;

public class MashedPotato extends ItemFood{
	
	public MashedPotato(){
		super(5, .3F, true);
		String name = "mashed_potato";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.tabCrossroads);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}
}

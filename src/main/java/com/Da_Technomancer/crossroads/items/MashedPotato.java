package com.Da_Technomancer.crossroads.items;

import net.minecraft.item.ItemFood;

public class MashedPotato extends ItemFood{
	
	public MashedPotato(){
		super(5, .3F, true);
		String name = "mashed_potato";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		CrossroadsItems.toRegister.add(this);
		CrossroadsItems.itemAddQue(this);
	}
}

package com.Da_Technomancer.crossroads.items;

import net.minecraft.item.ItemFood;

public class MashedPotato extends ItemFood{
	
	public MashedPotato(){
		super(5, .3F, true);
		String name = "mashed_potato";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		CRItems.toRegister.add(this);
	}
}

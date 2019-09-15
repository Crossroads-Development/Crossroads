package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.items.CrossroadsItems;

public class FlorenceFlask extends AbstractGlassware{

	private final boolean crystal;

	public FlorenceFlask(boolean crystal){
		String name = "florence_flask_" + (crystal ? "cryst" : "glass");
		this.crystal = crystal;
		maxStackSize = 1;
		hasSubtypes = true;
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		CrossroadsItems.toRegister.add(this);
		CrossroadsItems.itemAddQue(this);
	}

	@Override
	public boolean isCrystal(){
		return crystal;
	}

	@Override
	public int getCapacity(){
		return 100;
	}
}

package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.items.CRItems;

public class FlorenceFlask extends AbstractGlassware{

	private final boolean crystal;

	public FlorenceFlask(boolean crystal){
		String name = "florence_flask_" + (crystal ? "cryst" : "glass");
		this.crystal = crystal;
		maxStackSize = 1;
		hasSubtypes = true;
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		CRItems.toRegister.add(this);
		CRItems.itemAddQue(this);
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

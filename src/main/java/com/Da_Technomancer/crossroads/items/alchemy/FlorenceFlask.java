package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.items.CRItems;

public class FlorenceFlask extends AbstractGlassware{

	public FlorenceFlask(boolean crystal){
		super(GlasswareTypes.FLORENCE, crystal);
		String name = "florence_flask_" + (crystal ? "cryst" : "glass");
		CRItems.queueForRegister(name, this);
	}
}

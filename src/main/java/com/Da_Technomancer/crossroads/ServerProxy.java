package com.Da_Technomancer.crossroads;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy extends CommonProxy{

	public static int masterKey = 0;

	@Override
	protected void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
		
		if (Loader.isModLoaded("guideapi")){
			GuideBooks.mainGuide(e, false);
		}
	}

	@Override
	protected void init(FMLInitializationEvent e) {
		// TODO Auto-generated method stub
		super.init(e);
	}

	@Override
	protected void postInit(FMLPostInitializationEvent e) {
		// TODO Auto-generated method stub
		super.postInit(e);
	}

}

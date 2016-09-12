package com.Da_Technomancer.crossroads;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy extends CommonProxy{

	public static int masterKey = 0;

	@Override
	protected void preInit(FMLPreInitializationEvent e){
		super.preInit(e);

		if(Loader.isModLoaded("guideapi")){
			GuideBooks.mainGuide(false);
			GuideBooks.infoGuide(false);
		}
	}

	@Override
	protected void init(FMLInitializationEvent e){
		super.init(e);
	}

	@Override
	protected void postInit(FMLPostInitializationEvent e){
		super.postInit(e);
	}

}

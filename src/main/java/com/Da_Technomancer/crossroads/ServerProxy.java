package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.world.ModWorldGen;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
		GameRegistry.registerWorldGenerator(new ModWorldGen(), 0);
	}

	@Override
	protected void postInit(FMLPostInitializationEvent e){
		super.postInit(e);
	}

}

package com.Da_Technomancer.crossroads;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy extends CommonProxy{

	@Override
	protected void preInit(FMLPreInitializationEvent e){
		super.preInit(e);
	}

	@Override
	protected void init(FMLInitializationEvent e){
		super.init(e);
		MinecraftForge.EVENT_BUS.register(new EventHandlerServer());
	}

	@Override
	protected void postInit(FMLPostInitializationEvent e){
		super.postInit(e);
	}

}

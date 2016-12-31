package com.Da_Technomancer.crossroads;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Main.MODID, name = Main.MODNAME, version = Main.VERSION, dependencies = "required-after:Forge; after:guideapi; after:JEI", useMetadata = true)
public final class Main{

	public static final String MODID = "crossroads";
	public static final String MODNAME = "Crossroads";
	public static final String VERSION = "gradVERSION";

	static{
		FluidRegistry.enableUniversalBucket();
	}

	@SidedProxy(clientSide = "com.Da_Technomancer.crossroads.ClientProxy", serverSide = "com.Da_Technomancer.crossroads.ServerProxy")
	public static CommonProxy proxy;

	@Mod.Instance
	public static Main instance;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event){
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e){
		proxy.init(e);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e){
		proxy.postInit(e);
	}
}
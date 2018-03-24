package com.Da_Technomancer.crossroads;

import org.apache.logging.log4j.Logger;

import com.Da_Technomancer.crossroads.command.DiscoverElementCommand;
import com.Da_Technomancer.crossroads.command.ResetPathCommand;
import com.Da_Technomancer.crossroads.command.SpawnReagentCommand;
import com.Da_Technomancer.crossroads.command.WorkspaceDimTeleport;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;

import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = Main.MODID, name = Main.MODNAME, version = Main.VERSION, acceptedMinecraftVersions = "[1.12,1.13)", dependencies = "required-after:forge@[13.20.0.2271,]; before:guideapi; after:jei; after:crafttweaker")
public final class Main{

	public static final String MODID = "crossroads";
	public static final String MODNAME = "Crossroads";
	public static final String VERSION = "gradVERSION";

	static{
		FluidRegistry.enableUniversalBucket();
	}

	public static Logger logger;

	@SidedProxy(clientSide = "com.Da_Technomancer.crossroads.ClientProxy", serverSide = "com.Da_Technomancer.crossroads.ServerProxy")
	public static CommonProxy proxy;

	@Mod.Instance
	public static Main instance;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e){
		logger = e.getModLog();
		proxy.preInit(e);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e){
		proxy.init(e);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e){
		proxy.postInit(e);
	}

	@Mod.EventHandler
	public void serverLoading(FMLServerStartingEvent e){
		e.registerServerCommand(new WorkspaceDimTeleport());
		e.registerServerCommand(new ResetPathCommand());
		e.registerServerCommand(new DiscoverElementCommand());
		e.registerServerCommand(new SpawnReagentCommand());
	}

	@Mod.EventHandler
	public void serverStarted(FMLServerStartedEvent e){
		CommonProxy.masterKey = 1;
		ModDimensions.loadDims();

		//For singleplayer.
		ModConfig.syncPropNBT = ModConfig.nbtToSyncConfig();
	}

	@Mod.EventHandler
	public void serverEnded(FMLServerStoppingEvent e){
		ForgeChunkManager.releaseTicket(EventHandlerCommon.loadingTicket);
		EventHandlerCommon.loadingTicket = null;
	}
}
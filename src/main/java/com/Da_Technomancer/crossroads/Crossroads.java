package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.integration.CRIntegration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.Da_Technomancer.crossroads.Crossroads.MODID;

@Mod(MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Crossroads{

	public static final String MODID = "crossroads";
	public static final String MODNAME = "Crossroads";
	public static final Logger logger = LogManager.getLogger(MODNAME);

	public Crossroads(){
		final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::commonInit);
		bus.addListener(this::clientInit);
		bus.addListener(this::serverInit);

		CRConfig.init();

		MinecraftForge.EVENT_BUS.register(this);

		CRConfig.load();
	}

	private void commonInit(@SuppressWarnings("unused") FMLCommonSetupEvent e){
		//Pre
		CRPackets.init();
		//Main
		MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());

		CRIntegration.init();
	}

	private void clientInit(@SuppressWarnings("unused") FMLClientSetupEvent e){
		MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
	}

	private void serverInit(FMLDedicatedServerSetupEvent e){
		MinecraftForge.EVENT_BUS.register(new EventHandlerServer());
	}
}
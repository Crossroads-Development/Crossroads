package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.integration.CRIntegration;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
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
		final IEventBus bus = ModLoadingContext.get().getModEventBus();
		bus.addListener(this::commonInit);
		bus.addListener(this::clientInit);
		bus.addListener(this::serverInit);

		CRConfig.init();

		NeoForge.EVENT_BUS.register(this);

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
		CRBlocks.clientInit();
		CRItems.clientInit();
	}

	private void serverInit(FMLDedicatedServerSetupEvent e){
		MinecraftForge.EVENT_BUS.register(new EventHandlerServer());
	}
}
package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.entity.mob_effects.CRPotions;
import com.Da_Technomancer.crossroads.gui.container.CRContainers;
import com.Da_Technomancer.crossroads.integration.CRIntegration;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.Da_Technomancer.crossroads.Crossroads.MODID;

@Mod(MODID)
public final class Crossroads{

	public static final String MODID = "crossroads";
	public static final String MODNAME = "Crossroads";
	public static final Logger logger = LogManager.getLogger(MODNAME);

	public Crossroads(IEventBus bus, ModContainer modContainer){
		bus.addListener(this::commonInit);
		bus.addListener(this::clientInit);
		bus.addListener(this::serverInit);

		CRBlocks.init(bus);
		CRItems.init(bus);
		CRPotions.init(bus);
		CRContainers.init(bus);
		CRConfig.init(modContainer);
	}

	private void commonInit(@SuppressWarnings("unused") FMLCommonSetupEvent e){
		NeoForge.EVENT_BUS.register(new EventHandlerCommon());

		CRIntegration.init();
	}

	private void clientInit(@SuppressWarnings("unused") FMLClientSetupEvent e){
		NeoForge.EVENT_BUS.register(new EventHandlerClient());
		CRBlocks.clientInit();
		CRItems.clientInit();
	}

	private void serverInit(FMLDedicatedServerSetupEvent e){
		NeoForge.EVENT_BUS.register(new EventHandlerServer());
	}
}
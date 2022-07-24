package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.gui.container.CRContainers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

public class EventHandlerServer{

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Crossroads.MODID, value = Dist.DEDICATED_SERVER)
	public static class CRModEventsServer{

		@SuppressWarnings("unused")
		@SubscribeEvent
		public static void register(RegisterEvent e){
			e.register(ForgeRegistries.Keys.MENU_TYPES, helper -> {
				//The other half of this is in EventHandlerClient
				CRContainers.initServer();
				EventHandlerCommon.CRModEventsCommon.registerAll(helper, CRContainers.toRegisterMenu);
			});
		}
	}
}

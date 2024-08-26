package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.gui.container.CRContainers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class EventHandlerServer{

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Crossroads.MODID, value = Dist.DEDICATED_SERVER)
	public static class CRModEventsServer{

		@SuppressWarnings("unused")
		@SubscribeEvent
		public static void register(RegisterEvent e){
			//TODO look into way menus are handled now
			e.register(NeoForgeRegistries.Keys.MENU_TYPE, helper -> {
				//The other half of this is in EventHandlerClient
				CRContainers.initServer();
				EventHandlerCommon.CRModEventsCommon.registerAll(helper, CRContainers.toRegisterMenu);
			});
		}
	}
}

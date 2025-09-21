package com.Da_Technomancer.crossroads;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;

public class EventHandlerServer{

	@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Crossroads.MODID, value = Dist.DEDICATED_SERVER)
	public static class CRModEventsServer{

	}
}
